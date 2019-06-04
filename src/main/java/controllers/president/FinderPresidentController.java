
package controllers.president;

import java.util.Date;

import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ConfigurationService;
import services.FinderService;
import services.HiringService;
import services.PresidentService;
import services.SigningService;
import services.TeamService;
import controllers.AbstractController;
import domain.Actor;
import domain.Finder;
import domain.Manager;

@Controller
@RequestMapping("/finder/president")
public class FinderPresidentController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	PresidentService				presidentService;

	@Autowired
	private FinderService			finderService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TeamService				teamService;

	@Autowired
	private SigningService			signingService;

	@Autowired
	private HiringService			hiringService;


	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public ModelAndView showFinder() {
		ModelAndView result;
		final Finder finder;

		final Actor actor = this.actorService.findByPrincipal();
		finder = this.finderService.findFinderByPresident(actor.getId());

		final Date currentTime = new Date(System.currentTimeMillis() - 1000);
		final Interval interval = new Interval(finder.getLastUpdate().getTime(), currentTime.getTime());

		final Integer timeOut = this.configurationService.findConfiguration().getFinderTime();
		final Integer pagesize = this.configurationService.findConfiguration().getFinderResult();

		final String banner = this.configurationService.findConfiguration().getBanner();
		final String language = LocaleContextHolder.getLocale().getLanguage();

		if (interval.toDuration().getStandardHours() > timeOut)
			this.finderService.deleteManagersPlayers(finder);

		final Integer teamId = this.teamService.findByPresidentId(actor.getId()) != null ? this.teamService.findByPresidentId(actor.getId()).getId() : null;

		Integer numPlayers = null;
		Integer numManager = null;

		if (teamId != null) {
			numPlayers = this.signingService.findAllByPresident(actor.getId()).size();
			final Manager manager = this.teamService.findManagerByTeamId(teamId);
			if (manager != null)
				numManager = 1;
			else
				numManager = 0;
		}

		result = new ModelAndView("actor/listPlayerManager");
		result.addObject("players", finder.getPlayers());
		result.addObject("managers", finder.getManagers());
		result.addObject("finder", finder);
		result.addObject("requestURI", "finder/president/find.do");
		result.addObject("requestAction", "finder/president/find.do");
		result.addObject("banner", banner);
		result.addObject("pagesize", pagesize);
		result.addObject("AmILogged", true);
		result.addObject("AmInFinder", true);
		result.addObject("language", language);
		result.addObject("teamNow", teamId);
		result.addObject("numPlayers", numPlayers);
		result.addObject("numManager", numManager);

		return result;

	}
	@RequestMapping(value = "/find", method = RequestMethod.POST, params = "find")
	public ModelAndView editFinder(Finder finder, final BindingResult binding) {
		ModelAndView result;

		final Finder finderSearched = this.finderService.findOne(finder.getId());
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (finderSearched != null) {
			finder = this.finderService.reconstruct(finder, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(finder, null);
			else
				try {
					final Boolean areInputsDifferents = this.finderService.checkInputs(finder);
					if (areInputsDifferents)
						this.finderService.save(finder);
					result = new ModelAndView("redirect:find.do");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(finder, "finder.commit.error");

				}
		} else {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Finder finder) {
		ModelAndView result;

		result = this.createEditModelAndView(finder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Finder finder, final String messageCode) {
		final ModelAndView result;

		result = new ModelAndView("actor/listPlayerManager");

		final String banner = this.configurationService.findConfiguration().getBanner();
		final String language = LocaleContextHolder.getLocale().getLanguage();

		result.addObject("finder", finder);
		result.addObject("messageError", messageCode);
		result.addObject("banner", banner);
		result.addObject("AmILogged", true);
		result.addObject("AmInFinder", true);
		result.addObject("language", language);

		return result;
	}
}
