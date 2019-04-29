
package controllers.provider;

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

import services.ConfigurationService;
import services.ItemService;
import services.ProviderService;
import controllers.AbstractController;
import domain.Item;
import domain.Provider;

@Controller
@RequestMapping("/item/provider")
public class ItemProviderController extends AbstractController {

	@Autowired
	private ItemService				itemService;

	@Autowired
	private ProviderService			providerService;

	@Autowired
	private ConfigurationService	configurationService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int providerId) {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Provider notFound = this.providerService.findOne(providerId);

		if (notFound == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {

			final Collection<Item> items;

			items = this.itemService.findItemsByProviderId(providerId);

			result = new ModelAndView("item/list");
			result.addObject("items", items);
			result.addObject("requestURI", "item/listByProvider.do");
			result.addObject("pagesize", 5);
			result.addObject("banner", banner);
			result.addObject("language", LocaleContextHolder.getLocale().getLanguage());
			result.addObject("autoridad", "");

		}
		return result;

	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int itemId) {
		ModelAndView result;
		final Item item;
		Boolean security = false;

		final String banner = this.configurationService.findConfiguration().getBanner();

		final Boolean existItem = this.itemService.existId(itemId);

		if (existItem) {

			security = this.itemService.securityItem(itemId);

			if (security) {

				item = this.itemService.findOne(itemId);

				result = new ModelAndView("item/display");
				result.addObject("item", item);
				result.addObject("banner", banner);
				result.addObject("requestURI", "item/provider/display.do");

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
	public ModelAndView create() {
		final ModelAndView result;
		final String banner = this.configurationService.findConfiguration().getBanner();

		final Item item = this.itemService.create();

		result = this.createEditModelAndView(item);

		result.addObject("banner", banner);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int itemId) {
		ModelAndView result;
		Item item;
		Boolean security;

		final String banner = this.configurationService.findConfiguration().getBanner();
		final Item itemNotFound = this.itemService.findOne(itemId);

		if (itemNotFound == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			security = this.itemService.securityItem(itemId);

			if (security) {
				item = this.itemService.findOne(itemId);
				result = this.createEditModelAndView(item, null);
			} else
				result = new ModelAndView("redirect:/welcome/index.do");
		}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute(value = "item") Item item, final BindingResult binding) {
		ModelAndView result;

		item = this.itemService.reconstruct(item, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(item, null);
		else
			try {
				this.itemService.save(item);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(item, "item.commit.error");

			}

		return result;
	}

	//Delete--------------------------------------------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Item item, final BindingResult binding) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		if (item.getId() != 0 && this.itemService.findOne(item.getId()) == null) {
			result = new ModelAndView("misc/notExist");
			result.addObject("banner", banner);
		} else {
			item = this.itemService.findOne(item.getId());
			final Boolean security = this.itemService.securityItem(item.getId());

			if (security)
				try {
					this.itemService.delete(item);
					result = new ModelAndView("redirect:list.do");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(item, "item.commit.error");
				}
			else
				result = new ModelAndView("redirect:/welcome/index.do");
		}
		return result;
	}

	//Other business methods-------------------------------------------------
	protected ModelAndView createEditModelAndView(final Item item) {
		ModelAndView result;

		result = this.createEditModelAndView(item, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Item item, final String messageCode) {
		ModelAndView result;

		final String banner = this.configurationService.findConfiguration().getBanner();

		result = new ModelAndView("item/edit");
		result.addObject("item", item);
		result.addObject("messageError", messageCode);
		result.addObject("banner", banner);

		return result;
	}

}