/*
 * ProfileController.java
 * 
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.Credentials;
import services.ActorService;
import services.AdministratorService;
import services.ConfigurationService;
import services.FederationService;
import services.ManagerService;
import services.PlayerService;
import services.PresidentService;
import services.RefereeService;
import services.SponsorService;
import services.StatisticalDataService;
import domain.Actor;
import domain.Administrator;
import domain.Federation;
import domain.Manager;
import domain.Player;
import domain.President;
import domain.Referee;
import domain.Sponsor;
import domain.StatisticalData;

@Controller
@RequestMapping("/profile")
public class ProfileController extends AbstractController {

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private RefereeService			refereeService;

	@Autowired
	private FederationService		federationService;

	@Autowired
	private PresidentService		presidentService;

	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private StatisticalDataService	statisticalDataService;


	@RequestMapping(value = "/displayPrincipal", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Actor actor;
		final String banner = this.configurationService.findConfiguration().getBanner();
		actor = this.actorService.findOne(this.actorService.findByPrincipal().getId());
		try {
			Assert.notNull(actor);

			final Locale locale = LocaleContextHolder.getLocale();
			final String language = locale.getLanguage();

			result = new ModelAndView("actor/display");
			result.addObject("actor", actor);
			result.addObject("banner", banner);
			result.addObject("laguageURI", "profile/displayPrincipal.do");
			result.addObject("admin", false);
			result.addObject("language", language);

			final Authority authPlayer = new Authority();
			authPlayer.setAuthority(Authority.PLAYER);
			if (actor.getUserAccount().getAuthorities().contains(authPlayer)) {
				final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(actor.getId());
				result.addObject("statisticalData", statisticalData);
			}
		} catch (final Exception e) {
			result = new ModelAndView("misc/welcome");
			result.addObject("banner", banner);
		}

		return result;

	}
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit() {
		final ModelAndView result;
		final Actor actor;

		final Actor principal = this.actorService.findByPrincipal();
		actor = this.actorService.findOne(principal.getId());
		Assert.isTrue(actor.equals(principal));

		final Authority authority1 = new Authority();
		authority1.setAuthority(Authority.MANAGER);

		final Authority authority2 = new Authority();
		authority2.setAuthority(Authority.PLAYER);

		final Authority authority3 = new Authority();
		authority3.setAuthority(Authority.ADMIN);

		final Authority authority4 = new Authority();
		authority4.setAuthority(Authority.FEDERATION);

		final Authority authority5 = new Authority();
		authority5.setAuthority(Authority.PRESIDENT);

		final Authority authority6 = new Authority();
		authority6.setAuthority(Authority.SPONSOR);

		final Authority authority7 = new Authority();
		authority7.setAuthority(Authority.REFEREE);

		String auth = null;
		String action = null;
		if (actor.getUserAccount().getAuthorities().contains(authority1)) {
			auth = "manager";
			action = "editManager.do";

		} else if (actor.getUserAccount().getAuthorities().contains(authority2)) {
			auth = "player";
			action = "editPlayer.do";
		} else if (actor.getUserAccount().getAuthorities().contains(authority3)) {
			auth = "administrator";
			action = "editAdministrator.do";
		} else if (actor.getUserAccount().getAuthorities().contains(authority4)) {
			auth = "federation";
			action = "editFederation.do";
		} else if (actor.getUserAccount().getAuthorities().contains(authority5)) {
			auth = "president";
			action = "editPresident.do";
		} else if (actor.getUserAccount().getAuthorities().contains(authority6)) {
			auth = "sponsor";
			action = "editSponsor.do";
		} else if (actor.getUserAccount().getAuthorities().contains(authority7)) {
			auth = "referee";
			action = "editReferee.do";
		}

		final String banner = this.configurationService.findConfiguration().getBanner();
		final String defaultCountry = this.configurationService.findConfiguration().getCountryCode();
		result = new ModelAndView("actor/edit");
		result.addObject("actionURI", action);
		result.addObject(auth, actor);
		result.addObject("authority", auth);
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("defaultCountry", defaultCountry);
		final Collection<String> makes = this.configurationService.findConfiguration().getMakes();
		result.addObject("makes", makes);

		return result;
	}

	//--------------------------ADMINISTRATOR------------------------------

	@RequestMapping(value = "/editAdministrator", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAdministrator(@ModelAttribute("administrator") final Administrator admin, final BindingResult binding) {
		ModelAndView result;

		final Administrator adminReconstruct = this.administratorService.reconstruct(admin, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewAdmin(adminReconstruct);
		else
			try {
				this.administratorService.save(adminReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(adminReconstruct.getUserAccount().getUsername());
				credentials.setPassword(adminReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewAdmin(adminReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewAdmin(final Administrator admin) {
		ModelAndView result;

		result = this.createEditModelAndViewAdmin(admin, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewAdmin(final Administrator admin, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("administrator", admin);
		result.addObject("authority", "administrator");
		result.addObject("actionURI", "editAdministrator.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}

	//--------------------------MANAGER------------------------------

	@RequestMapping(value = "/editManager", method = RequestMethod.POST, params = "save")
	public ModelAndView saveManager(@ModelAttribute("manager") final Manager manager, final BindingResult binding) {
		ModelAndView result;

		final Manager managerReconstruct = this.managerService.reconstruct(manager, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewManager(managerReconstruct);
		else
			try {
				this.managerService.save(managerReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(managerReconstruct.getUserAccount().getUsername());
				credentials.setPassword(managerReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewManager(managerReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewManager(final Manager manager) {
		ModelAndView result;

		result = this.createEditModelAndViewManager(manager, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewManager(final Manager manager, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("manager", manager);
		result.addObject("authority", "manager");
		result.addObject("actionURI", "editManager.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}

	//--------------------------PLAYER------------------------------

	@RequestMapping(value = "/editPlayer", method = RequestMethod.POST, params = "save")
	public ModelAndView savePlayer(@ModelAttribute("player") final Player player, final BindingResult binding) {
		ModelAndView result;

		final Player playerReconstruct = this.playerService.reconstruct(player, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewPlayer(playerReconstruct);
		else
			try {
				this.playerService.save(playerReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(playerReconstruct.getUserAccount().getUsername());
				credentials.setPassword(playerReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewPlayer(playerReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewPlayer(final Player player) {
		ModelAndView result;

		result = this.createEditModelAndViewPlayer(player, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewPlayer(final Player player, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("player", player);
		result.addObject("authority", "player");
		result.addObject("actionURI", "editPlayer.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}

	//--------------------------FEDERATION------------------------------

	@RequestMapping(value = "/editFederation", method = RequestMethod.POST, params = "save")
	public ModelAndView saveFederation(@ModelAttribute("federation") final Federation federation, final BindingResult binding) {
		ModelAndView result;

		final Federation federationReconstruct = this.federationService.reconstruct(federation, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewFederation(federationReconstruct);
		else
			try {
				this.federationService.save(federationReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(federationReconstruct.getUserAccount().getUsername());
				credentials.setPassword(federationReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewFederation(federationReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewFederation(final Federation federation) {
		ModelAndView result;

		result = this.createEditModelAndViewFederation(federation, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewFederation(final Federation federation, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("federation", federation);
		result.addObject("authority", "federation");
		result.addObject("actionURI", "editFederation.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}

	//--------------------------SPONSOR------------------------------

	@RequestMapping(value = "/editSponsor", method = RequestMethod.POST, params = "save")
	public ModelAndView saveSponsor(@ModelAttribute("sponsor") final Sponsor sponsor, final BindingResult binding) {
		ModelAndView result;

		final Sponsor sponsorReconstruct = this.sponsorService.reconstruct(sponsor, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewSponsor(sponsorReconstruct);
		else
			try {
				this.sponsorService.save(sponsorReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(sponsorReconstruct.getUserAccount().getUsername());
				credentials.setPassword(sponsorReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewSponsor(sponsorReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewSponsor(final Sponsor sponsor) {
		ModelAndView result;

		result = this.createEditModelAndViewSponsor(sponsor, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewSponsor(final Sponsor sponsor, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("sponsor", sponsor);
		result.addObject("authority", "sponsor");
		result.addObject("actionURI", "editSponsor.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);
		final Collection<String> makes = this.configurationService.findConfiguration().getMakes();
		result.addObject("makes", makes);

		return result;
	}

	//--------------------------PRESIDENT------------------------------

	@RequestMapping(value = "/editPresident", method = RequestMethod.POST, params = "save")
	public ModelAndView savePresident(@ModelAttribute("president") final President president, final BindingResult binding) {
		ModelAndView result;

		final President presidentReconstruct = this.presidentService.reconstruct(president, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewPresident(presidentReconstruct);
		else
			try {
				this.presidentService.save(presidentReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(presidentReconstruct.getUserAccount().getUsername());
				credentials.setPassword(presidentReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewPresident(presidentReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewPresident(final President president) {
		ModelAndView result;

		result = this.createEditModelAndViewPresident(president, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewPresident(final President president, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("president", president);
		result.addObject("authority", "president");
		result.addObject("actionURI", "editPresident.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}

	//--------------------------REFEREE------------------------------

	@RequestMapping(value = "/editReferee", method = RequestMethod.POST, params = "save")
	public ModelAndView saveReferee(@ModelAttribute("referee") final Referee referee, final BindingResult binding) {
		ModelAndView result;

		final Referee refereeReconstruct = this.refereeService.reconstruct(referee, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndViewReferee(refereeReconstruct);
		else
			try {
				this.refereeService.save(refereeReconstruct);
				final Credentials credentials = new Credentials();
				credentials.setJ_username(refereeReconstruct.getUserAccount().getUsername());
				credentials.setPassword(refereeReconstruct.getUserAccount().getPassword());
				result = new ModelAndView("redirect:/profile/displayPrincipal.do");
				result.addObject("credentials", credentials);
			} catch (final Throwable oops) {
				result = this.createEditModelAndViewReferee(refereeReconstruct, "actor.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndViewReferee(final Referee referee) {
		ModelAndView result;

		result = this.createEditModelAndViewReferee(referee, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewReferee(final Referee referee, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("actor/edit");

		result.addObject("referee", referee);
		result.addObject("authority", "referee");
		result.addObject("actionURI", "editReferee.do");
		result.addObject("banner", banner);
		result.addObject("laguageURI", "profile/edit.do");
		result.addObject("messageError", messageCode);
		final String countryCode = this.configurationService.findConfiguration().getCountryCode();
		result.addObject("defaultCountry", countryCode);

		return result;
	}
}
