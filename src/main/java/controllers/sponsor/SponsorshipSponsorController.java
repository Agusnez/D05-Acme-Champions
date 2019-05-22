
package controllers.sponsor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.SponsorService;
import services.SponsorshipService;
import controllers.AbstractController;
import domain.Sponsor;
import domain.Sponsorship;

@Controller
@RequestMapping("/sponsorship/sponsor")
public class SponsorshipSponsorController extends AbstractController {

	//Services -----------------------------------------------------------

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private SponsorService			sponsorService;


	//List----------------------------------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Sponsorship> sponsorships;

		final Sponsor sponsor = this.sponsorService.findByPrincipal();

		sponsorships = this.sponsorshipService.findAllBySponsorId(sponsor.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("sponsorship/list");
		result.addObject("sponsorships", sponsorships);
		result.addObject("requestURI", "sponsorship/sponsor/list.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);

		return result;

	}

	//Display------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView show(@RequestParam final int sponsorshipId) {
		final ModelAndView result;
		final Sponsorship sponsorship;
		final Sponsor login;

		final String banner = this.configurationService.findConfiguration().getBanner();
		final Sponsorship sponsorshipNotFound = this.sponsorshipService.findOne(sponsorshipId);

		if (sponsorshipNotFound == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {

			login = this.sponsorService.findByPrincipal();

			sponsorship = this.sponsorshipService.findOne(sponsorshipId);

			if (login == sponsorship.getSponsor()) {

				result = new ModelAndView("sponsorship/display");
				result.addObject("sponsorship", sponsorship);
				result.addObject("banner", banner);

			} else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;
	}

	//	//Create------------------------------------------------------------------
	//	@RequestMapping(value = "/sponsor", method = RequestMethod.GET)
	//	public ModelAndView create(@RequestParam final int positionId) {
	//		final ModelAndView result;
	//		final String banner = this.configurationService.findConfiguration().getBanner();
	//
	//		final Position position = this.positionService.findOne(positionId);
	//
	//		if (position == null || this.positionService.findOne(positionId).getCancellation() != null) {
	//
	//			result = new ModelAndView("misc/notExist");
	//			result.addObject("banner", banner);
	//
	//		} else if (position.getFinalMode() == true) {
	//			final Integer sponsorshipId = this.sponsorshipService.findSponsorshipByPositionAndProviderId(positionId, this.providerService.findByPrincipal().getId());
	//
	//			if (sponsorshipId != null) {
	//
	//				final Sponsorship sponsorship = this.sponsorshipService.findOne(sponsorshipId);
	//
	//				final SponsorshipForm sponsorshipForm = this.sponsorshipService.editForm(sponsorship);
	//				result = this.createEditModelAndView(sponsorshipForm, null);
	//
	//			} else {
	//
	//				final SponsorshipForm sponsorship = this.sponsorshipService.create(positionId);
	//
	//				result = new ModelAndView("sponsorship/edit");
	//				result.addObject("sponsorship", sponsorship);
	//				result.addObject("banner", banner);
	//
	//			}
	//		} else {
	//			result = new ModelAndView("redirect:/welcome/index.do");
	//
	//			result.addObject("banner", banner);
	//		}
	//
	//		return result;
	//
	//	}
	//
	//	//Edit--------------------------------------------------------------------
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView edit(@RequestParam final int sponsorshipId) {
	//		ModelAndView result;
	//		Sponsorship sponsorship;
	//		SponsorshipForm sponsorshipForm;
	//		Boolean security;
	//
	//		final String banner = this.configurationService.findConfiguration().getBanner();
	//
	//		sponsorship = this.sponsorshipService.findOne(sponsorshipId);
	//
	//		if (sponsorship == null || this.sponsorshipService.findOne(sponsorshipId).getPosition().getCancellation() != null) {
	//			result = new ModelAndView("misc/notExist");
	//			result.addObject("banner", banner);
	//		} else {
	//
	//			security = this.sponsorshipService.sponsorshipSponsorSecurity(sponsorshipId);
	//
	//			if (security) {
	//
	//				sponsorshipForm = this.sponsorshipService.editForm(sponsorship);
	//				result = this.createEditModelAndView(sponsorshipForm, null);
	//
	//			} else {
	//				result = new ModelAndView("redirect:/welcome/index.do");
	//
	//				result.addObject("banner", banner);
	//			}
	//		}
	//		return result;
	//	}
	//
	//	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	//	public ModelAndView save(@ModelAttribute(value = "sponsorship") final SponsorshipForm sponsorshipform, final BindingResult binding) {
	//		ModelAndView result;
	//		Sponsorship sponsorship = null;
	//
	//		try {
	//
	//			sponsorship = this.sponsorshipService.reconstruct(sponsorshipform, binding);
	//
	//		} catch (final Exception e) {
	//
	//		}
	//		Boolean security = false;
	//
	//		if (sponsorship != null && sponsorship.getId() == 0)
	//			security = true;
	//		else if (sponsorship != null && sponsorship.getId() != 0)
	//			security = this.sponsorshipService.sponsorshipSponsorSecurity(sponsorship.getId());
	//
	//		if (security) {
	//
	//			if (binding.hasErrors())
	//				result = this.createEditModelAndView(sponsorshipform, null);
	//			else
	//				try {
	//					this.sponsorshipService.save(sponsorship);
	//					result = new ModelAndView("redirect:/sponsorship/provider/list.do");
	//				} catch (final Throwable oops) {
	//					result = this.createEditModelAndView(sponsorshipform, "sponsorship.commit.error");
	//
	//				}
	//
	//		} else
	//			result = new ModelAndView("redirect:/welcome/index.do");
	//
	//		return result;
	//	}
	//
	//	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	//	public ModelAndView delete(@ModelAttribute(value = "sponsorship") final SponsorshipForm sponsorshipform, final BindingResult binding) {
	//		ModelAndView result;
	//		Sponsorship sponsorship = null;
	//		try {
	//
	//			sponsorship = this.sponsorshipService.reconstruct(sponsorshipform, binding);
	//
	//		} catch (final Exception e) {
	//
	//		}
	//		Boolean security = false;
	//
	//		if (sponsorship != null && sponsorship.getId() == 0)
	//			security = true;
	//		else if (sponsorship != null && sponsorship.getId() != 0)
	//			security = this.sponsorshipService.sponsorshipSponsorSecurity(sponsorship.getId());
	//
	//		if (security) {
	//
	//			if (binding.hasErrors())
	//				result = this.createEditModelAndView(sponsorshipform, null);
	//			else
	//				try {
	//					this.sponsorshipService.delete(sponsorship);
	//					result = new ModelAndView("redirect:/sponsorship/provider/list.do");
	//				} catch (final Throwable oops) {
	//					result = this.createEditModelAndView(sponsorshipform, "sponsorship.commit.error");
	//
	//				}
	//
	//		} else
	//			result = new ModelAndView("redirect:/welcome/index.do");
	//
	//		return result;
	//	}
	//	//Other business methods---------------------------------------------------------------------------------------------
	//	protected ModelAndView createEditModelAndView(final SponsorshipForm sponsorship, final String messageCode) {
	//		final ModelAndView result;
	//
	//		final String banner = this.configurationService.findConfiguration().getBanner();
	//
	//		result = new ModelAndView("sponsorship/edit");
	//		result.addObject("sponsorship", sponsorship);
	//		result.addObject("messageError", messageCode);
	//		result.addObject("banner", banner);
	//		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
	//
	//		return result;
	//
	//	}

}
