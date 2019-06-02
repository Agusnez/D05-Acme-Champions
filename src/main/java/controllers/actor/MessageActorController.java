
package controllers.actor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.BoxService;
import services.ConfigurationService;
import services.MessageService;
import controllers.AbstractController;
import domain.Actor;
import domain.Message;
import forms.MessageForm;

@Controller
@RequestMapping("/message/actor")
public class MessageActorController extends AbstractController {

	@Autowired
	private MessageService			messageService;

	@Autowired
	private BoxService				boxService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int boxId) {
		ModelAndView result;
		final Collection<Message> messages;
		Boolean security;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.boxService.existId(boxId);

		if (exist) {

			security = this.boxService.boxSecurity(boxId);

			if (security) {

				messages = this.messageService.findMessagesByBoxId(boxId);

				result = new ModelAndView("message/list");
				result.addObject("messages", messages);
				result.addObject("banner", banner);
				result.addObject("boxId", boxId);
				result.addObject("requestURI", "message/actor/list.do");

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

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int messageId, @RequestParam final int boxId) {
		ModelAndView result;
		final Message message1;
		final Boolean security;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean existMessage = this.messageService.existId(messageId);
		final Boolean existBox = this.boxService.existId(boxId);

		if (existMessage && existBox) {

			security = this.messageService.securityMessage(boxId);

			if (security) {

				message1 = this.messageService.findOne(messageId);

				result = new ModelAndView("message/display");
				result.addObject("message1", message1);
				result.addObject("box", this.boxService.findOne(boxId));
				result.addObject("banner", banner);
				result.addObject("requestURI", "message/actor/display.do");

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
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int actorId) {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean exist = this.actorService.existActor(actorId);
		if (exist) {
			final Boolean diferent = this.actorService.findByPrincipal().getId() != actorId;
			if (diferent) {
				final MessageForm message2 = this.messageService.create(actorId);

				result = this.createEditModelAndView(message2);
			} else
				result = new ModelAndView("redirect:/welcome/index.do");

		} else {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "message") final MessageForm message2, final BindingResult binding) {

		ModelAndView result;
		Boolean exist1, exist2, exist3;
		exist1 = this.actorService.existActor(message2.getSenderId());
		exist2 = this.actorService.existActor(message2.getRecipientId());
		exist3 = message2.getId() == 0;
		if (exist1 && exist2 && exist3) {
			final Message message3 = this.messageService.reconstruct(message2, binding);
			if (binding.hasErrors())
				result = this.createEditModelAndView(message2);
			else
				try {

					final Boolean cond1 = (message3.getSender() == this.actorService.findByPrincipal());
					final Boolean cond2 = (message3.getRecipient() != this.actorService.findByPrincipal());
					final Boolean cond3 = (message3.getId() == 0);

					if (cond1 && cond2 && cond3) {
						this.messageService.save(message3);
						result = new ModelAndView("redirect:/box/actor/list.do");
					} else
						result = new ModelAndView("redirect:/welcome/index.do");

				} catch (final Throwable oops) {
					result = this.createEditModelAndView(message2, "message.commit.error");
				}
		} else
			result = new ModelAndView("redirect:/welcome/index.do");
		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int messageId, @RequestParam final int boxId) {

		ModelAndView result;
		final Message message1;
		final Boolean security1, security2;

		final Actor actor = this.actorService.findByPrincipal();

		final Boolean existMessage = this.messageService.existId(messageId);
		final Boolean existBox = this.boxService.existId(boxId);
		final String banner = this.configurationService.findConfiguration().getBanner();

		if (existMessage && existBox) {

			security1 = this.messageService.securityMessage(boxId);

			if (security1) {

				message1 = this.messageService.findOne(messageId);

				security2 = message1.getRecipient().equals(actor) || message1.getSender().equals(actor);

				if (security2) {

					this.messageService.delete(message1);

					result = new ModelAndView("redirect:/box/actor/list.do");
					result.addObject("banner", banner);
				} else {

					result = new ModelAndView("redirect:/welcome/index.do");
					result.addObject("banner", banner);
				}

			} else
				result = new ModelAndView("redirect:/welcome/index.do");
			result.addObject("banner", banner);

		} else
			result = new ModelAndView("misc/notExist");
		result.addObject("banner", banner);

		return result;
	}

	protected ModelAndView createEditModelAndView(final MessageForm message2) {
		final ModelAndView result;
		result = this.createEditModelAndView(message2, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final MessageForm message2, final String errorText) {
		final ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("message/create");
		result.addObject("messageError", errorText);
		result.addObject("message", message2);
		result.addObject("banner", banner);
		result.addObject("enlace", "message/actor/edit.do");

		return result;
	}
}
