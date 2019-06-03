package controllers.federation;

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

import domain.Actor;
import domain.Competition;
import domain.Federation;
import domain.Format;
import domain.Game;
import domain.Minutes;
import domain.Team;
import services.ActorService;
import services.CompetitionService;
import services.ConfigurationService;
import services.FederationService;
import services.FormatService;

@Controller
@RequestMapping("/format/federation")
public class FormatFederationController {

	// Services ---------------------------------------------------

	@Autowired
	private FormatService formatService;

	@Autowired
	private FederationService federationService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private ActorService actorService;

	@Autowired
	private CompetitionService competitionService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Format> formats;
		final Federation federation;

		federation = this.federationService.findByPrincipal();

		formats = this.formatService.findFormatByFederationId(federation.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("format/list");
		result.addObject("formats", formats);
		result.addObject("requestURI", "/format/federation/list.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		result.addObject("autoridad", "federation");

		return result;

	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		try {
			final String banner = this.configurationService.findConfiguration().getBanner();

			final Actor actor = this.actorService.findByPrincipal();

			Format format = this.formatService.create();

			result = this.createEditModelAndView(format, null);
			result.addObject("banner", banner);

			return result;
		} catch (Exception e) {
			final String banner = this.configurationService.findConfiguration().getBanner();

			result = new ModelAndView("misc/error");
			result.addObject("banner", banner);
			return result;
		}

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int formatId) {
		ModelAndView result;
		try {
			final Format format = this.formatService.findOne(formatId);

			final Actor actor = this.actorService.findByPrincipal();

			final String banner = this.configurationService.findConfiguration().getBanner();

			if (format == null || (format.getId() != 0 && format.getFederation().getId() != actor.getId())) {
				result = new ModelAndView("misc/notExist");
				result.addObject("banner", banner);
			} else
				result = this.createEditModelAndView(format, null);
			return result;
		} catch (Exception e) {
			final String banner = this.configurationService.findConfiguration().getBanner();

			result = new ModelAndView("misc/error");
			result.addObject("banner", banner);
			return result;
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "format") Format format, final BindingResult binding) {
		ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (format.getId() != 0 && this.formatService.findOne(format.getId()) == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			format = this.formatService.reconstruct(format, binding);
			if (binding.hasErrors())
				result = this.createEditModelAndView(format, null);
			else
				try {
					this.formatService.save(format);
					result = new ModelAndView("redirect:/format/federation/list.do");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(format, "commit.error");

				}
		}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Format format, final BindingResult binding) {
		ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		try {
			final Actor actor = this.actorService.findByPrincipal();
			int id = format.getId();
			format = this.formatService.findOne(id);

			if (format == null) {
				result = new ModelAndView("misc/notExist");
				result.addObject("banner", banner);
			} else {

				Collection<Competition> c = this.competitionService.findByFormatId(format.getId());

				if (format == null || (format.getId() != 0 && format.getFederation().getId() != actor.getId())
						|| !c.isEmpty()) {
					result = new ModelAndView("misc/notExist");
					result.addObject("banner", banner);
				} else
					try {
						this.formatService.delete(format);
						result = new ModelAndView("redirect:/format/federation/list.do");
					} catch (final Throwable oops) {
						result = this.createEditModelAndView(format, "commit.error");

					}
				
			}
			return result;
		} catch (Exception e) {
			result = new ModelAndView("misc/error");
			result.addObject("banner", banner);
			return result;
		}
	}

	protected ModelAndView createEditModelAndView(final Format format, final String messageCode) {
		final ModelAndView result;

		int id = format.getId();

		Collection<Competition> c = this.competitionService.findByFormatId(id);

		int canDelete = 0;

		if (c.isEmpty()) {
			canDelete = 1;
		}

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("format/edit");
		result.addObject("format", format);
		result.addObject("banner", banner);
		result.addObject("canDelete", canDelete);
		result.addObject("messageError", messageCode);

		return result;
	}

}
