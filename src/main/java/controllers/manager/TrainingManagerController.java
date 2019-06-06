
package controllers.manager;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import services.ActorService;
import services.ConfigurationService;
import services.ManagerService;
import services.PlayerService;
import services.TrainingService;
import controllers.AbstractController;
import domain.Actor;
import domain.Manager;
import domain.Player;
import domain.Training;

@Controller
@RequestMapping("/training/manager")
public class TrainingManagerController extends AbstractController {

	@Autowired
	private TrainingService			trainingService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;


	//Create-----------------------------------------------------------------------------------------
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Actor actor = this.actorService.findByPrincipal();
		final Authority authority = new Authority();
		authority.setAuthority("MANAGER");
		if (actor.getUserAccount().getAuthorities().contains(authority)) {
			final Training training = this.trainingService.create();
			result = this.createEditModelAndView(training, null);
		} else {
			result = new ModelAndView("redirect:/welcome/index.do");
			result.addObject("banner", banner);
		}
		return result;

	}

	//Edit-------------------------------------------------------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int trainingId) {
		ModelAndView result;
		Boolean security;

		final Training trainingFind = this.trainingService.findOne(trainingId);
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (trainingFind == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			final Boolean startDateGood = this.checkStartDate(trainingFind);
			security = this.trainingService.trainingManagerSecurity(trainingId);

			if (security && startDateGood)
				result = this.createEditModelAndView(trainingFind, null);
			else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "training") Training training, final BindingResult binding) {
		ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (training.getId() != 0 && this.trainingService.findOne(training.getId()) == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else if ((training.getId() != 0 && this.trainingService.findOne(training.getId()).getManager() != this.managerService.findByPrincipal()) || (training.getId() != 0 && this.checkStartDate(this.trainingService.findOne(training.getId())) == false))
			result = new ModelAndView("redirect:/welcome/index.do");
		else {

			training = this.trainingService.reconstruct(training, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(training, null);
			else
				try {
					this.trainingService.save(training);
					result = new ModelAndView("redirect:/calendar/manager/show.do");
				} catch (final Throwable oops) {
					if (oops.getMessage() == "Invalid Dates")
						result = this.createEditModelAndView(training, "url.error");
					else
						result = this.createEditModelAndView(training, "training.commit.error");
				}
		}
		return result;
	}
	//Delete--------------------------------------------------------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Training training, final BindingResult binding) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();
		if (this.trainingService.findOne(training.getId()) == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			training = this.trainingService.findOne(training.getId());
			final Manager manager = this.managerService.findByPrincipal();
			final Boolean startDateGood = this.checkStartDate(training);

			if (training.getManager().getId() == manager.getId() && startDateGood)
				try {
					this.trainingService.delete(training);
					result = new ModelAndView("redirect:/calendar/manager/show.do");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(training, "training.commit.error");
				}
			else
				result = new ModelAndView("redirect:/welcome/index.do");

		}
		return result;
	}
	//Display------------------------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int trainingId) {
		ModelAndView result;
		Boolean security;

		final Training trainingFind = this.trainingService.findOne(trainingId);
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (trainingFind == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			security = this.trainingService.trainingManagerSecurity(trainingId);

			if (security) {
				final Boolean startDateGood = this.checkStartDate(trainingFind);
				result = new ModelAndView("training/display");
				result.addObject("training", trainingFind);
				result.addObject("banner", banner);
				result.addObject("startDateGood", startDateGood);
			} else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;
	}

	//Add Player to Training------------------------------------------------------------
	@RequestMapping(value = "/addPlayer", method = RequestMethod.GET)
	public ModelAndView addPlayer(@RequestParam final int trainingId) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();
		final Training training = this.trainingService.findOne(trainingId);

		if (training == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			final Boolean security = this.trainingService.trainingManagerSecurity(trainingId);
			final Boolean startDateGood = this.checkStartDate(training);
			if (security && startDateGood) {
				final Collection<Player> playersOfTheTeam = this.playerService.findPlayersOfTeam(training.getManager().getTeam().getId());
				playersOfTheTeam.removeAll(training.getPlayers());

				final String language = LocaleContextHolder.getLocale().getLanguage();

				result = new ModelAndView("player/listAdd");
				result.addObject("players", playersOfTheTeam);
				result.addObject("requestURI", "training/manager/addPlayer.do");
				result.addObject("pagesize", 5);
				result.addObject("banner", banner);
				result.addObject("trainingId", trainingId);
				result.addObject("language", language);

			} else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;

	}
	@RequestMapping(value = "/addPlayerPost", method = RequestMethod.GET)
	public ModelAndView addPlayerPost(@RequestParam final int playerId, @RequestParam final int trainingId) {
		ModelAndView result;
		final Player player = this.playerService.findOne(playerId);
		final Training training = this.trainingService.findOne(trainingId);
		final String banner = this.configurationService.findConfiguration().getBanner();
		final Boolean security1;
		Boolean security2;
		final String language = LocaleContextHolder.getLocale().getLanguage();

		if (player == null || training == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			if (player.getTeam() != null)
				security1 = player.getTeam().getId() == this.managerService.findByPrincipal().getTeam().getId();
			else
				security1 = false;
			security2 = this.trainingService.trainingManagerSecurity(trainingId);
			final Boolean startDateGood = this.checkStartDate(training);

			if (security1 && security2 && startDateGood)
				try {
					this.trainingService.addPlayerToTraining(player, training);

					final Collection<Player> playersResult = this.playerService.findPlayersOfTeam(training.getManager().getTeam().getId());
					final Training trainingNew = this.trainingService.findOne(trainingId);
					playersResult.removeAll(trainingNew.getPlayers());

					result = new ModelAndView("player/listAdd");
					result.addObject("players", playersResult);
					result.addObject("requestURI", "training/manager/addPlayer.do");
					result.addObject("pagesize", 5);
					result.addObject("banner", banner);
					result.addObject("trainingId", trainingId);
					result.addObject("language", language);

				} catch (final Throwable oops) {

					final Collection<Player> playersResult = this.playerService.findPlayersOfTeam(training.getManager().getTeam().getId());
					final Training trainingNew = this.trainingService.findOne(trainingId);
					playersResult.removeAll(trainingNew.getPlayers());

					result = new ModelAndView("player/listAdd");
					result.addObject("players", playersResult);
					result.addObject("requestURI", "training/manager/addPlayer.do");
					result.addObject("pagesize", 5);
					result.addObject("messageError", "player.addToTraining.error");
					result.addObject("banner", banner);
					result.addObject("trainingId", trainingId);
					result.addObject("language", language);
				}
			else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;
	}

	//Ancillary methods---------------------------------------------------------------------------------
	protected ModelAndView createEditModelAndView(final Training training, final String messageCode) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("training/edit");
		result.addObject("training", training);
		result.addObject("messageError", messageCode);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		return result;
	}

	public Boolean checkStartDate(final Training training) {
		final Date actual = new Date();
		Boolean goodDate = true;
		if (training.getStartDate().before(actual) || training.getStartDate().equals(actual))
			goodDate = false;
		return goodDate;

	}
}
