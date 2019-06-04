
package controllers.anonymous;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.GameService;
import services.MinutesService;
import services.SponsorshipService;
import controllers.AbstractController;
import domain.Game;
import domain.Minutes;
import domain.Sponsorship;

@Controller
@RequestMapping("/game")
public class GameController extends AbstractController {

	@Autowired
	private GameService				gameService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private MinutesService			minutesService;

	@Autowired
	private SponsorshipService		sponsorshipService;


	//List para todo el mundo ------------------------------------------------------------------------------
	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	public ModelAndView list() {

		final ModelAndView result;
		final Collection<Game> games;

		games = this.gameService.findAllGamesOrdered();

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("game/list");
		result.addObject("games", games);
		result.addObject("requestURI", "game/listAll.do");
		result.addObject("pagesize", 5);
		result.addObject("banner", banner);
		result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
		result.addObject("autoridad", "");

		return result;

	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int gameId) {
		ModelAndView result;

		final Game gameFind = this.gameService.findOne(gameId);
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (gameFind == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			final Minutes minutesByGame = this.minutesService.findMinuteByGameId(gameId);
			Boolean haveMinutesClosed = false;

			if (minutesByGame != null)
				if (minutesByGame.getClosed())
					haveMinutesClosed = true;

			result = new ModelAndView("game/display");
			result.addObject("game", gameFind);
			result.addObject("banner", banner);
			result.addObject("haveMinutesClosed", haveMinutesClosed);
			if (haveMinutesClosed)
				result.addObject("minutesByGame", minutesByGame);

			//Esto es para ver los sponsorships
			final Collection<Sponsorship> sponsorships = this.sponsorshipService.findSponsorshipsByGameId(gameId);
			if (sponsorships != null && !sponsorships.isEmpty())
				result.addObject("sponsorships", sponsorships);
		}
		return result;
	}
}
