
package controllers.manager;

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
import domain.Hiring;
import domain.Manager;
import forms.HiringForm;

@Controller
@RequestMapping("/hiring/manager")
public class HiringManagerController extends AbstractController {

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
		final Manager manager;

		manager = this.managerService.findByPrincipal();

		hirings = this.hiringService.findByManager(manager.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("hiring/list");
		result.addObject("hirings", hirings);
		result.addObject("requestURI", "hiring/manager/list.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		result.addObject("autoridad", "manager");

		return result;

	}

	@RequestMapping(value = "/accept", method = RequestMethod.GET)
	public ModelAndView accept(@RequestParam final int hiringId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.hiringService.exist(hiringId);

		if (exist) {

			final Hiring hiring = this.hiringService.findOne(hiringId);

			final Manager loged = this.managerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (hiring.getManager().equals(loged) && loged.getTeam() == null && hiring.getStatus().equals("PENDING"))
					try {

						final Manager manager = this.managerService.findOne(hiring.getManager().getId());

						manager.setTeam(this.teamService.findByPresidentId(hiring.getPresident().getId()));

						final Collection<Hiring> oldOnes = this.hiringService.findAllByManager(manager.getId());

						for (final Hiring oldOne : oldOnes)
							this.hiringService.delete(oldOne);

						this.managerService.save(manager);

						this.teamService.functional(this.teamService.findByPresidentId(hiring.getPresident().getId()));

						hiring.setStatus("ACCEPTED");

						this.hiringService.save(hiring);

						result = new ModelAndView("redirect:/hiring/manager/list.do");
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
	public ModelAndView reject(@RequestParam final int hiringId) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.hiringService.exist(hiringId);

		if (exist) {

			final Hiring hiring = this.hiringService.findOne(hiringId);

			final Manager loged = this.managerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (hiring.getManager().equals(loged) && loged.getTeam() == null && hiring.getStatus().equals("PENDING")) {

					final HiringForm hiringForm = this.hiringService.editForm(hiring);

					result = this.createEditModelAndView(hiringForm);
					result.addObject("enlace", "hiring/manager/reject.do");

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
	public ModelAndView reject(@ModelAttribute(value = "hiring") final HiringForm hiringForm, final BindingResult binding) {
		ModelAndView result;

		final Boolean exist = this.hiringService.exist(hiringForm.getId());

		if (exist) {

			final Hiring hiring = this.hiringService.reconstruct(hiringForm, binding);

			final Manager loged = this.managerService.findByPrincipal();

			if (loged.getTeam() == null) {

				if (hiring.getManager().equals(loged) && loged.getTeam() == null && hiring.getStatus().equals("PENDING")) {

					if (binding.hasErrors())
						result = this.createEditModelAndView(hiringForm, null);
					else
						try {
							hiring.setStatus("REJECTED");
							this.hiringService.save(hiring);
							result = new ModelAndView("redirect:/hiring/manager/list.do");

						} catch (final Throwable oops) {

							result = this.createEditModelAndView(hiringForm, "hiring.commit.error");

						}

				} else
					result = new ModelAndView("redirect:/welcome/index.do");

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
		result.addObject("autoridad", "manager");

		return result;
	}

}
