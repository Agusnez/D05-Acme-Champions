
package controllers.administrator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ConfigurationService;
import controllers.AbstractController;
import domain.Actor;

@Controller
@RequestMapping("/actor/administrator")
public class AdministratorController extends AbstractController {

	//Services

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;


	//Methods

	@RequestMapping(value = "/profile/list", method = RequestMethod.GET)
	public ModelAndView listProfile() {

		final ModelAndView result;
		final Collection<Actor> actors;

		actors = this.actorService.findAll();

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("administrator/listActor");
		result.addObject("score", false);
		result.addObject("spam", false);
		result.addObject("admin", true);
		result.addObject("actors", actors);
		result.addObject("requestURI", "actor/administrator/profile/list.do");
		result.addObject("banner", banner);

		return result;

	}

	@RequestMapping(value = "profile/displayActor", method = RequestMethod.GET)
	public ModelAndView displayProfile(@RequestParam final int actorId) {
		ModelAndView result;
		Actor actor;

		final Actor principal = this.actorService.findByPrincipal();
		final Boolean exist = this.actorService.existActor(actorId);

		if (exist) {

			actor = this.actorService.findOne(actorId);
			Assert.notNull(actor);

			final String banner = this.configurationService.findConfiguration().getBanner();

			result = new ModelAndView("actor/display");
			result.addObject("actor", actor);
			result.addObject("principal", principal);
			result.addObject("banner", banner);
			result.addObject("admin", true);

		} else {
			result = new ModelAndView("misc/notExist");
			final String banner = this.configurationService.findConfiguration().getBanner();
			result.addObject("banner", banner);
		}
		return result;

	}

}
