
package controllers.president;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.HiringService;
import services.ManagerService;
import services.PlayerService;
import services.PresidentService;
import services.SigningService;
import services.TeamService;
import services.TrainingService;
import controllers.AbstractController;
import domain.Hiring;
import domain.Manager;
import domain.Player;
import domain.President;
import domain.Signing;
import domain.Team;
import domain.Training;

@Controller
@RequestMapping("/president")
public class FiringPresidentController extends AbstractController {

	// Services ---------------------------------------------------

	@Autowired
	PresidentService				presidentService;

	@Autowired
	PlayerService					playerService;

	@Autowired
	ManagerService					managerService;

	@Autowired
	private TeamService				teamService;

	@Autowired
	private SigningService			signingService;

	@Autowired
	private HiringService			hiringService;

	@Autowired
	private TrainingService			trainingService;

	@Autowired
	private ConfigurationService	configurationService;


	@RequestMapping(value = "/firePlayer", method = RequestMethod.GET)
	public ModelAndView firePlayer(@RequestParam final int playerId) {
		ModelAndView result = null;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final President president = this.presidentService.findByPrincipal();

		final Team team = this.teamService.findTeamByPresidentId(president.getId());

		final Player player = this.playerService.findOne(playerId);

		Signing signing;
		if (player != null && team != null) {
			final Collection<Player> players = this.playerService.findPlayersOfTeam(team.getId());
			if (players.contains(player)) {
				signing = this.signingService.findSigningOfPresidentAndPlayer(president.getId(), player.getId());

				try {
					this.signingService.delete(signing);
					player.setTeam(null);
					this.playerService.save(player);

					this.teamService.functional(team);

					result = new ModelAndView("redirect:/team/president,manager/listByPresident.do");
				} catch (final Exception oops) {

				}
			} else {
				// NO AUTORIZADO!
				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);
			}
		} else {
			// NO EXISTE EL JUGADOR!
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}
		return result;

	}

	@RequestMapping(value = "/fireManager", method = RequestMethod.GET)
	public ModelAndView fireManager(@RequestParam final int managerId) {
		ModelAndView result = null;

		final President president = this.presidentService.findByPrincipal();

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Team team = this.teamService.findTeamByPresidentId(president.getId());

		final Manager manager = this.managerService.findOne(managerId);

		final Hiring hiring;

		if (manager != null) {
			hiring = this.hiringService.findHiringOfPresidentAndManager(president.getId(), manager.getId());

			if (hiring != null) {
				this.hiringService.delete(hiring);

				try {
					final Collection<Training> trainings = this.trainingService.findFutureTrainingsByManagerId(manager.getId());

					for (final Training t : trainings)
						this.trainingService.delete(t);

					manager.setTeam(null);
					this.managerService.save(manager);

					team.setFunctional(false);
					this.teamService.save(team);

					result = new ModelAndView("redirect:/team/president,manager/listByPresident.do");
				} catch (final Exception oops) {

				}
			} else {
				// NO AUTORIZADO!
				result = new ModelAndView("redirect:/welcome/index.do");
				result.addObject("banner", banner);
			}
		} else {
			// NO EXISTE EL MANAGER!
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}

		return result;

	}

}
