
package controllers.president;

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
import services.HiringService;
import services.ManagerService;
import services.PresidentService;
import services.TeamService;
import controllers.AbstractController;
import domain.Actor;
import domain.Hiring;
import domain.Manager;
import domain.President;
import domain.Team;
import forms.HiringForm;

@Controller
@RequestMapping("/hiring/president")
public class HiringPresidentController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	private HiringService			hiringService;

	@Autowired
	private PresidentService		presidentService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TeamService				teamService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Hiring> hirings;
		final President president;

		final String banner = this.configurationService.findConfiguration().getBanner();

		president = this.presidentService.findByPrincipal();

		if (this.teamService.findByPresidentId(president.getId()) != null) {

			hirings = this.hiringService.findByPresident(president.getId());

			result = new ModelAndView("hiring/list");
			result.addObject("hirings", hirings);
			result.addObject("requestURI", "hiring/president/list.do");
			result.addObject("pagesize", 5);
			result.addObject("banner", banner);
			result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
			result.addObject("autoridad", "president");

		} else {

			result = new ModelAndView("misc/noTeam");
			result.addObject("banner", banner);

		}

		return result;

	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int managerId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		if (this.teamService.findByPresidentId(this.presidentService.findByPrincipal().getId()) != null) {

			if (this.teamService.findManagerByTeamId(this.teamService.findByPresidentId(this.presidentService.findByPrincipal().getId()).getId()) == null) {

				final Boolean exist = this.managerService.exist(managerId);

				if (exist) {

					final Manager manager = this.managerService.findOne(managerId);
					final Team team = this.teamService.findByPresidentId(this.presidentService.findByPrincipal().getId());

					if (manager.getTeam() == null || !manager.getTeam().equals(team)) {

						final HiringForm hiringForm = this.hiringService.create(managerId);
						result = this.createEditModelAndView(hiringForm);
						result.addObject("enlace", "hiring/president/edit.do");

					} else
						result = new ModelAndView("redirect:/welcome/index.do");

				} else {

					result = new ModelAndView("misc/notExist");
					result.addObject("banner", banner);
				}

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else {

			result = new ModelAndView("misc/noTeam");
			result.addObject("banner", banner);

		}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "hiring") final HiringForm hiringForm, final BindingResult binding) {
		ModelAndView result;

		final Boolean exist = this.managerService.exist(hiringForm.getManagerId());

		if (exist) {

			final Manager manager = this.managerService.findOne(hiringForm.getManagerId());
			final Team team = this.teamService.findByPresidentId(this.presidentService.findByPrincipal().getId());

			if (manager.getTeam() == null || !manager.getTeam().equals(team)) {

				if (this.teamService.findManagerByTeamId(this.teamService.findByPresidentId(this.presidentService.findByPrincipal().getId()).getId()) == null) {

					if (hiringForm.getId() == 0) {

						final Hiring hiring = this.hiringService.reconstruct(hiringForm, binding);

						if (binding.hasErrors())
							result = this.createEditModelAndView(hiringForm, null);
						else
							try {

								this.hiringService.save(hiring);
								result = new ModelAndView("redirect:/finder/president/find.do");

							} catch (final Throwable oops) {

								result = this.createEditModelAndView(hiringForm, "hiring.commit.error");

							}

					} else
						result = new ModelAndView("redirect:/welcome/index.do");

				} else
					result = new ModelAndView("redirect:/welcome/index.do");

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else
			result = new ModelAndView("redirect:/welcome/index.do");

		return result;
	}

	@RequestMapping(value = "/accept", method = RequestMethod.GET)
	public ModelAndView accept(@RequestParam final int hiringId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.hiringService.exist(hiringId);

		if (exist) {

			final Hiring hiring = this.hiringService.findOne(hiringId);

			final Actor loged = this.actorService.findByPrincipal();

			final Team team = this.teamService.findByPresidentId(loged.getId());

			if (hiring.getManager().getTeam() != null && hiring.getManager().getTeam().equals(team) && hiring.getStatus().equals("PENDING")) {

				final Manager manager = this.managerService.findOne(hiring.getManager().getId());

				try {

					this.managerService.editTeam(manager, this.teamService.findByPresidentId(hiring.getPresident().getId()));

					final Collection<Hiring> oldOnes = this.hiringService.findAllByManager(manager.getId());

					for (final Hiring oldOne : oldOnes)
						this.hiringService.delete(oldOne);

					this.teamService.functional(this.teamService.findByPresidentId(hiring.getPresident().getId()));

					hiring.setStatus("ACCEPTED");

					this.hiringService.save(hiring);

					result = new ModelAndView("redirect:/hiring/president/list.do");
					result.addObject("banner", banner);

				} catch (final Exception e) {

					result = new ModelAndView("misc/error");
					result.addObject("banner", banner);

				}

			} else {

				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);
			}

		} else {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.GET)
	public ModelAndView reject(@RequestParam final int hiringId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.hiringService.exist(hiringId);

		if (exist) {

			final Hiring hiring = this.hiringService.findOne(hiringId);

			final Actor loged = this.actorService.findByPrincipal();

			final Team team = this.teamService.findByPresidentId(loged.getId());

			if (hiring.getManager().getTeam() != null && hiring.getManager().getTeam().equals(team) && hiring.getStatus().equals("PENDING")) {

				final HiringForm hiringForm = this.hiringService.editForm(hiring);

				result = this.createEditModelAndView(hiringForm);
				result.addObject("enlace", "hiring/president/reject.do");

			} else {

				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);
			}

		} else {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.POST, params = "save")
	public ModelAndView reject(@ModelAttribute(value = "hiring") final HiringForm hiringForm, final BindingResult binding) {
		ModelAndView result;

		final Boolean exist = this.hiringService.exist(hiringForm.getId());

		if (exist) {

			final Hiring hiring = this.hiringService.reconstruct(hiringForm, binding);

			final Actor loged = this.actorService.findByPrincipal();

			final Team team = this.teamService.findByPresidentId(loged.getId());

			if (hiring.getManager().getTeam() != null && hiring.getManager().getTeam().equals(team) && hiring.getStatus().equals("PENDING")) {

				if (binding.hasErrors())
					result = this.createEditModelAndView(hiringForm, null);
				else
					try {
						hiring.setStatus("REJECTED");
						this.hiringService.save(hiring);
						result = new ModelAndView("redirect:/hiring/president/list.do");

					} catch (final Throwable oops) {

						result = this.createEditModelAndView(hiringForm, "hiring.commit.error");

					}

			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else
			result = new ModelAndView("redirect:/welcome/index.do");

		return result;
	}

	protected ModelAndView createEditModelAndView(final HiringForm hiring) {
		final ModelAndView result;
		result = this.createEditModelAndView(hiring, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final HiringForm hiring, final String errorText) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("hiring/edit");
		result.addObject("messageError", errorText);
		result.addObject("hiring", hiring);
		result.addObject("banner", banner);
		result.addObject("autoridad", "president");

		return result;
	}

}
