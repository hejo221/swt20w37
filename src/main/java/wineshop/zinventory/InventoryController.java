package wineshop.zinventory;

import org.apache.juli.logging.Log;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wineshop.wine.*;

import java.util.Optional;

@Controller
class InventoryController {

	private final UniqueInventory<UniqueInventoryItem> inventory;
	private static final Logger LOG = LoggerFactory.getLogger(InventoryInitializer.class);

	InventoryController(UniqueInventory<UniqueInventoryItem> inventory) {
		this.inventory = inventory;
	}

	@GetMapping("/inventory")
	String stock(Model model) {

		model.addAttribute("stock", inventory.findAll());

		return "/inventory/inventory";
	}

	@PostMapping("/inventory/update")
	String updateAmount(@RequestParam("item") ProductIdentifier bsp, @RequestParam("number") int number) {

		/*
		TODO:	Diese Funktion soll einfach nur die Qunatity eines Items auf den in number gespeicherten Wert setzen.

		TODO: 	Etwas anders als in den Screenshots, aber das gleiche Problem. Hier möchte ich den ProductIdentifier
		TODO:   vom Product des momentanen IventoryItems greifen. Laut den LOG-Einträgen bekomme ich auch etwas, was
		TODO:	danach aussieht, jedoch stimmt vermutlich der Datentyp nicht, wodurch die Funktionen unten einfach
		TODO:	ins Leere führen und nichts ändern. Wenn ich versuche, in der html nur ${item} zu speichern und es hier
		TODO:	abrufe, bekomme ich in der item.decreaseQuantity()-Funktion eine NullPointerException, was Sinn ergibt,
		TODO:	wenn das InventoryItem nicht als ein Solches übergeben wird.

		TODO:	Interessant wäre vielleicht noch, dass inventory.findAll(), welche für das Anzeigen der Itemsn (bzw. deren
		TODO:	Infos) im Lager genutzt wird, Streamable<UniqueInventoryItem> returnt. Vielleicht gehe ich damit auch noch
		TODO:	falsch um.
		 */
		UniqueInventoryItem item = inventory.findByProductIdentifier(bsp).get();
		
		LOG.info("Eingabewert-item:");
		LOG.info(String.valueOf(bsp));
		LOG.info("Eingabewert-number:");
		LOG.info(String.valueOf(number));

		item.decreaseQuantity(item.getQuantity());
		item.increaseQuantity(Quantity.of(number));

		return "redirect:/inventory";


	}
}