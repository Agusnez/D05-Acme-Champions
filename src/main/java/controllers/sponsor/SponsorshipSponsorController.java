
package controllers.sponsor;

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

import services.ConfigurationService;
import services.GameService;
import services.PlayerService;
import services.SponsorService;
import services.SponsorshipService;
import services.TeamService;
import controllers.AbstractController;
import domain.Game;
import domain.Player;
import domain.Sponsor;
import domain.Sponsorship;
import domain.Team;

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

	@Autowired
	private TeamService				teamService;

	@Autowired
	private GameService				gameService;

	@Autowired
	private PlayerService			playerService;


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

	//List (Con esta lista vemos los posibles elementos a los que podemos hacer un sponsorship) ----------------------------------------------------------
	@RequestMapping(value = "/listCreate", method = RequestMethod.GET)
	public ModelAndView listCreate() {

		final ModelAndView result;
		final Collection<Team> teams;
		final Collection<Game> games;
		final Collection<Player> players;

		final Sponsor sponsor = this.sponsorService.findByPrincipal();

		teams = this.sponsorshipService.findTeamsAvailableToBeSponsored(sponsor.getId());
		games = this.sponsorshipService.findGamesAvailableToBeSponsored(sponsor.getId());
		players = this.sponsorshipService.findPlayersAvailableToBeSponsored(sponsor.getId());

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("sponsorship/listCreate");
		result.addObject("teams", teams);
		result.addObject("games", games);
		result.addObject("players", players);
		result.addObject("requestURI", "sponsorship/sponsor/listCreate.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);

		return result;

	}

	//Crear Sponsorship para Team ----------------------------------------------------------------------------
	@RequestMapping(value = "/sponsorTeam", method = RequestMethod.GET)
	public ModelAndView createTeam(@RequestParam final int teamId) {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Team team = this.teamService.findOne(teamId);

		if (team == null) {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);

		} else {
			final Integer sponsorshipId = this.sponsorshipService.findSponsorshipByTeamAndSponsorId(teamId, this.sponsorService.findByPrincipal().getId());

			if (sponsorshipId != null) {

				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);

			} else {

				final Sponsorship sponsorship = this.sponsorshipService.createWithTeam(teamId);

				result = new ModelAndView("sponsorship/edit");
				result.addObject("sponsorship", sponsorship);
				result.addObject("banner", banner);
			}
		}
		return result;

	}

	//Crear Sponsorship para Player ----------------------------------------------------------------------------
	@RequestMapping(value = "/sponsorPlayer", method = RequestMethod.GET)
	public ModelAndView createPlayer(@RequestParam final int playerId) {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Player player = this.playerService.findOne(playerId);

		if (player == null) {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);

		} else {
			final Integer sponsorshipId = this.sponsorshipService.findSponsorshipByPlayerAndSponsorId(playerId, this.sponsorService.findByPrincipal().getId());

			if (sponsorshipId != null) {

				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);

			} else {

				final Sponsorship sponsorship = this.sponsorshipService.createWithPlayer(playerId);

				result = new ModelAndView("sponsorship/edit");
				result.addObject("sponsorship", sponsorship);
				result.addObject("banner", banner);
			}
		}
		return result;

	}

	//Crear Sponsorship para Game ----------------------------------------------------------------------------
	@RequestMapping(value = "/sponsorGame", method = RequestMethod.GET)
	public ModelAndView createGame(@RequestParam final int gameId) {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Game game = this.gameService.findOne(gameId);

		if (game == null) {

			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);

		} else {
			final Integer sponsorshipId = this.sponsorshipService.findSponsorshipByGameAndSponsorId(gameId, this.sponsorService.findByPrincipal().getId());

			if (sponsorshipId != null) {

				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);

			} else {

				final Sponsorship sponsorship = this.sponsorshipService.createWithGame(gameId);

				result = new ModelAndView("sponsorship/edit");
				result.addObject("sponsorship", sponsorship);
				result.addObject("banner", banner);
			}
		}
		return result;

	}

	//Save-------------------------------------------------------------------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("sponsorship") final Sponsorship sponsorship, final BindingResult binding) {
		ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		Sponsorship sponsorship2;

		sponsorship2 = this.sponsorshipService.reconstruct(sponsorship, binding);

		Integer sponsorshipId = null;
		Integer sponsorshipId1 = null;
		Integer sponsorshipId2 = null;
		Boolean wrongDate = false;

		if (sponsorship2.getGame() != null) {
			sponsorshipId = this.sponsorshipService.findSponsorshipByGameAndSponsorId(sponsorship2.getGame().getId(), this.sponsorService.findByPrincipal().getId());
			final Date now = new Date();
			wrongDate = sponsorship2.getGame().getGameDate().before(now);

		}
		if (sponsorship2.getPlayer() != null)
			sponsorshipId1 = this.sponsorshipService.findSponsorshipByPlayerAndSponsorId(sponsorship2.getPlayer().getId(), this.sponsorService.findByPrincipal().getId());

		if (sponsorship2.getTeam() != null)
			sponsorshipId2 = this.sponsorshipService.findSponsorshipByTeamAndSponsorId(sponsorship2.getTeam().getId(), this.sponsorService.findByPrincipal().getId());

		if (sponsorshipId != null || sponsorshipId1 != null || sponsorshipId2 != null || wrongDate == true) {

			result = new ModelAndView("redirect:/welcome/index.do");
			result.addObject("banner", banner);

		} else if (binding.hasErrors())

			if (sponsorship2.getCreditCard() == null || sponsorship2.getSponsor() == null)
				result = this.createEditModelAndView(sponsorship, "sponsorship.id.error");
			else
				result = this.createEditModelAndView(sponsorship, null);

		else
			try {
				this.sponsorshipService.save(sponsorship2);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(sponsorship2, "sponsorship.commit.error");
			}

		return result;
	}

	//	//Other business methods---------------------------------------------------------------------------------------------
	protected ModelAndView createEditModelAndView(final Sponsorship sponsorship, final String messageCode) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("sponsorship/edit");
		result.addObject("sponsorship", sponsorship);
		result.addObject("messageError", messageCode);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());

		return result;

	}

}
