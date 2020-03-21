package codingdojo;

import java.util.ArrayList;

/**
 * The online shopping company owns a chain of Stores selling
 * makeup and beauty products.
 * <p>
 * Customers using the online shopping website can choose a Store then
 * can put Items available at that store into their Cart.
 * <p>
 * If no store is selected, then items are shipped from
 * a central warehouse.
 */
public class OnlineShopping {

    private Session session;

    public OnlineShopping(Session session) {
        this.session = session;
    }

    /**
     * This method is called when the user changes the
     * store they are shopping at in the online shopping
     * website.
     *
     */
    public void switchStore(Store storeToSwitchTo) {
        Cart cart = (Cart) session.get("CART");
        DeliveryInformation deliveryInformation = (DeliveryInformation) session.get("DELIVERY_INFO");
        Store currentStore = (Store) session.get("STORE");
        LocationService locationService = (LocationService) session.get("LOCATION_SERVICE");

        if (storeToSwitchTo == null) {
            switchToCentralWarehouse(cart, deliveryInformation);
        } else {
            switchTo(storeToSwitchTo, cart, deliveryInformation, currentStore, locationService);
        }
        session.put("STORE", storeToSwitchTo);
        session.saveAll();
    }

    private void switchTo(Store storeToSwitchTo, Cart cart, DeliveryInformation deliveryInformation, Store currentStore, LocationService locationService) {
        if (cart == null) {
            return;
        }
        ArrayList<Item> newItems = new ArrayList<>();
        long weight = 0;
        for (Item item : cart.getItems()) {
            if ("EVENT".equals(item.getType())) {
                if (storeToSwitchTo.hasItem(item)) {
                    cart.markAsUnavailable(item);
                    newItems.add(storeToSwitchTo.getItem(item.getName()));
                } else {
                    cart.markAsUnavailable(item);
                }
            } else if (!storeToSwitchTo.hasItem(item)) {
                cart.markAsUnavailable(item);
            }
            weight += item.getWeight();
        }
        for (Item item: cart.getUnavailableItems()) {
            weight -= item.getWeight();
        }
        for (Item item : newItems) {
            cart.addItem(item);
        }

        if (deliveryInformation != null
                && deliveryInformation.getType() != null
                && "HOME_DELIVERY".equals(deliveryInformation.getType())
                && deliveryInformation.getDeliveryAddress() != null) {
            if (!locationService.isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress())) {
                deliveryInformation.setType("PICKUP");
                deliveryInformation.setPickupLocation(currentStore);
            } else {
                deliveryInformation.setTotalWeight(weight);
                deliveryInformation.setPickupLocation(storeToSwitchTo);
            }
        } else {
            if (deliveryInformation != null
                    && deliveryInformation.getDeliveryAddress() != null) {
                if (locationService.isWithinDeliveryRange(storeToSwitchTo, deliveryInformation.getDeliveryAddress())) {
                    deliveryInformation.setType("HOME_DELIVERY");
                    deliveryInformation.setTotalWeight(weight);
                    deliveryInformation.setPickupLocation(storeToSwitchTo);

                }
            }
        }
    }

    private void switchToCentralWarehouse(Cart cart, DeliveryInformation deliveryInformation) {
        if (cart != null) {
            for (Item item : cart.getItems()) {
                if ("EVENT".equals(item.getType())) {
                    cart.markAsUnavailable(item);
                }
            }

        }
        if (deliveryInformation != null) {
            deliveryInformation.setType("SHIPPING");
            deliveryInformation.setPickupLocation(null);
        }
    }

    @Override
    public String toString() {
        return "OnlineShopping{\n"
                + "session=" + session + "\n}";
    }
}
