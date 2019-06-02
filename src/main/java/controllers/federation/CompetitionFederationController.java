
package controllers.federation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import services.CompetitionService;
import services.ConfigurationService;
import services.FederationService;
import services.FormatService;
import services.TeamService;
import domain.Actor;
import domain.Competition;
import domain.Federation;
import domain.Format;
import domain.Team;
import forms.CompetitionForm;

@Controller
@RequestMapping("/competition/federation")
public class CompetitionFederationController {

	// Services ---------------------------------------------------

	@Autowired
	private FormatService			formatService;

	@Autowired
	private FederationService		federationService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private CompetitionService		competitionService;

	@Autowired
	private TeamService				teamService;

	@Autowired
	private ActorService			actorService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Competition> competitions;
		final Federation federation;

		federation = this.federationService.findByPrincipal();

		competitions = this.competitionService.findByFederationId(federation.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("competition/list");
		result.addObject("competitions", competitions);
		result.addObject("requestURI", "competition/federation/list.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		result.addObject("autoridad", "federation");

		return result;

	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;

		final CompetitionForm competitionForm = this.competitionService.create();

		result = this.createEditModelAndView(competitionForm);
		result.addObject("enlace", "competition/federation/edit.do");

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "competition") final CompetitionForm competitionForm, final BindingResult binding) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		if (competitionForm.getId() == 0) {

			final Boolean exist = this.formatService.exist(competitionForm.getFormatId());

			if (exist) {

				final Boolean security = this.formatService.security(competitionForm.getFormatId());

				if (security) {

					final Competition competition = this.competitionService.reconstruct(competitionForm, binding);

					if (binding.hasErrors())
						result = this.createEditModelAndView(competitionForm, null);
					else
						try {

							this.competitionService.save(competition);
							result = new ModelAndView("redirect:/competition/federation/list.do");

						} catch (final Throwable oops) {

							result = this.createEditModelAndView(competitionForm, "competition.commit.error");

						}

				} else
					result = new ModelAndView("redirect:/welcome/index.do");

			} else {

				result = new ModelAndView("misc/notExist");
				result.addObject("banner", banner);
			}

		} else
			result = new ModelAndView("redirect:/welcome/index.do");

		return result;
	}
	@RequestMapping(value = "/close", method = RequestMethod.GET)
	public ModelAndView close(@RequestParam final Integer competitionId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist2 = this.competitionService.exist(competitionId);

		if (exist2) {

			final Boolean security = this.competitionService.security(competitionId);

			if (security) {

				final Competition competition = this.competitionService.findOne(competitionId);

				final Date now = new Date();

				if (competition.getStartDate().after(now)) {

					if (competition.getFormat().getType().equals("LEAGUE") && competition.getTeams().size() >= 2 && competition.getTeams().size() >= competition.getFormat().getMinimumTeams()
						&& competition.getTeams().size() <= competition.getFormat().getMaximumTeams())
						try {

							competition.setClosed(true);

							this.competitionService.save(competition);

							result = new ModelAndView("redirect:/competition/federation/list.do");

						} catch (final Exception e) {

							result = new ModelAndView("misc/error");
							result.addObject("banner", banner);

						}
					else if ((Math.log(competition.getTeams().size()) / Math.log(2)) % 1 == 0 && competition.getTeams().size() >= competition.getFormat().getMinimumTeams() && competition.getTeams().size() <= competition.getFormat().getMaximumTeams())
						try {

							competition.setClosed(true);

							this.competitionService.save(competition);

							result = new ModelAndView("redirect:/competition/federation/list.do");

						} catch (final Exception e) {

							result = new ModelAndView("misc/error");
							result.addObject("banner", banner);

						}
					else {

						final Collection<Competition> competitions;
						final Federation federation;

						federation = this.federationService.findByPrincipal();

						competitions = this.competitionService.findByFederationId(federation.getId());

						result = new ModelAndView("competition/list");
						result.addObject("competitions", competitions);
						result.addObject("requestURI", "competition/federation/list.do");
						result.addObject("pagesize", 5);
						result.addObject("banner", banner);
						result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
						result.addObject("autoridad", "federation");

						result.addObject("teamError", true);

					}

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

	@RequestMapping(value = "/listAddTeam", method = RequestMethod.GET)
	public ModelAndView listAddTeam(@RequestParam final Integer competitionId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist2 = this.competitionService.exist(competitionId);

		if (exist2) {

			final Boolean security = this.competitionService.security(competitionId);

			if (security) {

				final Competition competition = this.competitionService.findOne(competitionId);

				final Collection<Team> teamsNow = competition.getTeams();
				final Collection<Team> teamsAll = this.teamService.findFunctionalTeams();

				teamsAll.removeAll(teamsNow);

				result = new ModelAndView("competition/listAddTeam");
				result.addObject("competitionId", competitionId);
				result.addObject("teamsAll", teamsAll);
				result.addObject("requestURI", "competition/fedearation/list.do");
				result.addObject("pagesize", 5);
				result.addObject("banner", banner);
				result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
				result.addObject("autoridad", "federation");

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	@RequestMapping(value = "/addTeam", method = RequestMethod.GET)
	public ModelAndView addTeam(@RequestParam final Integer teamId, @RequestParam final Integer competitionId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.teamService.exist(teamId);
		final Boolean exist2 = this.competitionService.exist(competitionId);

		if (exist && exist2) {

			final Boolean security = this.competitionService.security(competitionId);

			if (security) {

				final Team team = this.teamService.findOne(teamId);

				final Competition competition = this.competitionService.findOne(competitionId);

				if (team.getFunctional() && !competition.getTeams().contains(team))
					try {

						this.competitionService.addTeam(competition, team);

						result = new ModelAndView("redirect:/competition/federation/listAddTeam.do");
						result.addObject("competitionId", competitionId);

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

	protected ModelAndView createEditModelAndView(final CompetitionForm competition) {
		final ModelAndView result;
		result = this.createEditModelAndView(competition, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final CompetitionForm competition, final String errorText) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Actor actor = this.actorService.findByPrincipal();

		final Collection<Format> formats = this.formatService.findFormatByFederationId(actor.getId());

		final Map<Integer, String> formatMap = new HashMap<>();

		for (final Format format : formats)
			formatMap.put(format.getId(), format.getType() + "max" + format.getMaximumTeams() + "min" + format.getMinimumTeams());

		result = new ModelAndView("competition/edit");
		result.addObject("competition", competition);
		result.addObject("messageError", errorText);
		result.addObject("banner", banner);
		result.addObject("autoridad", "federation");
		result.addObject("formatMap", formatMap);

		return result;
	}

}
