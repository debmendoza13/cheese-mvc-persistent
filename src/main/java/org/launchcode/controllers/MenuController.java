//package org.launchcode.models;

package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu menu, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        } else {

            //model.addAttribute("title", menu.getName());
            //model.addAttribute(menu);

            //missing code?????

            menuDao.save(menu);
            return "redirect:view/" + menu.getId();
        }
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable("id") int menuId) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute(menu);
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("menuId", menu.getId());
        // more code needed?

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable("id") int menuId) {
        Menu menu = menuDao.findOne(menuId);

        Iterable<Cheese> cheeses = cheeseDao.findAll();

        AddMenuItemForm form = new AddMenuItemForm(menu, cheeses);

        model.addAttribute("form", form);
        model.addAttribute("title", "Add Item to menu: " + menu.getName());

        return "menu/add-item";
    }
    @RequestMapping(value = "add-item/", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm form, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item to menu" + form.getMenu().getName());
            model.addAttribute("form", form);
            return "menu/add-item";
        } else {
            Menu menu = menuDao.findOne(form.getMenuId());
            menu.addItem(cheeseDao.findOne(form.getCheeseId()));
            menuDao.save(menu);

            return "redirect:./view/" + menu.getId();
        }
    }

}