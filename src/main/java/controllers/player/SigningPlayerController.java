
package controllers.player;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ConfigurationService;
import services.PlayerService;
import services.PresidentService;
import services.SigningService;
import services.TeamService;
import controllers.AbstractController;
import domain.Player;
import domain.Signing;
import forms.SigningForm;

@Controller
@RequestMapping("/signing/player")
public class SigningPlayerController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	private SigningService			signingService;

	@Autowired
	private PresidentService		presidentService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TeamService				teamService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Signing> signings;
		final Player player;

		player = this.playerService.findByPrincipal();

		signings = this.signingService.findByPlayer(player.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("signing/list");
		result.addObject("signings", signings);
		result.addObject("requestURI", "signing/player/list.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		result.addObject("autoridad", "player");

		return result;

	}

	@RequestMapping(value = "/accept", method = RequestMethod.GET)
	public ModelAndView accept(@RequestParam final int signingId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.signingService.exist(signingId);

		if (exist) {

			final Signing signing = this.signingService.findOne(signingId);
			final Player loged = this.playerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (signing.getPlayer().equals(loged) && loged.getTeam() == null && signing.getStatus().equals("PENDING"))
					try {

						final Player player = this.playerService.findOne(signing.getPlayer().getId());

						player.setTeam(this.teamService.findByPresidentId(signing.getPresident().getId()));
						player.setBuyoutClause(signing.getOfferedClause());

						this.playerService.save(player);

						this.teamService.functional(this.teamService.findByPresidentId(signing.getPresident().getId()));

						final Collection<Signing> oldOnes = this.signingService.findAllByPlayer(player.getId());

						for (final Signing oldOne : oldOnes)
							this.signingService.delete(oldOne);

						this.teamService.functional(this.teamService.findByPresidentId(signing.getPresident().getId()));

						signing.setStatus("ACCEPTED");

						this.signingService.save(signing);

						result = new ModelAndView("redirect:/signing/player/list.do");
						result.addObject("banner", banner);

					} catch (final Exception e) {

						result = new ModelAndView("misc/error");
						result.addObject("banner", banner);

					}
				else
					result = new ModelAndView("redirect:/welcome/index.do");

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.GET)
	public ModelAndView reject(@RequestParam final int signingId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.signingService.exist(signingId);

		if (exist) {

			final Signing signing = this.signingService.findOne(signingId);

			final Player loged = this.playerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (signing.getPlayer().equals(loged) && loged.getTeam() == null && signing.getStatus().equals("PENDING")) {

					final SigningForm signingForm = this.signingService.editForm(signing);

					result = this.createEditModelAndView(signingForm);
					result.addObject("enlace", "signing/player/reject.do");

				} else
					result = new ModelAndView("redirect:/welcome/index.do");
			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST, params = "save")
	public ModelAndView reject(@ModelAttribute(value = "signing") final SigningForm signingForm, final BindingResult binding) {
		ModelAndView result;

		final Boolean exist = this.signingService.exist(signingForm.getId());

		if (exist) {

			final Signing signing = this.signingService.reconstruct(signingForm, binding);

			final Player loged = this.playerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (signing.getPlayer().equals(loged) && loged.getTeam() == null && signing.getStatus().equals("PENDING")) {

					if (binding.hasErrors())
						result = this.createEditModelAndView(signingForm, null);
					else
						try {
							signing.setStatus("REJECTED");
							this.signingService.save(signing);
							result = new ModelAndView("redirect:/signing/player/list.do");

						} catch (final Throwable oops) {

							result = this.createEditModelAndView(signingForm, "signing.commit.error");

						}

				} else
					result = new ModelAndView("redirect:/welcome/index.do");

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else
			result = new ModelAndView("redirect:/welcome/index.do");

		return result;
	}

	protected ModelAndView createEditModelAndView(final SigningForm signing) {
		final ModelAndView result;
		result = this.createEditModelAndView(signing, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final SigningForm signing, final String errorText) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("signing/edit");
		result.addObject("messageError", errorText);
		result.addObject("signing", signing);
		result.addObject("banner", banner);
		result.addObject("autoridad", "player");

		return result;
	}

}
