package codingdojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnlineShoppingTest {

    private Store backaplan;
    private Store nordstan;

    private Item cherryBloom;
    private Item rosePetal;
    private Item blusherBrush;
    private Item eyelashCurler;
    private Item wildRose;
    private Item cocoaButter;
    private Item masterclass;
    private Item makeoverNordstan;
    private Item makeoverBackaplan;
    private LocationService locationService = new LocationService();

    @BeforeEach
    public void setUp() {
        nordstan = new Store("Nordstan", false);
        backaplan = new Store("Backaplan", true);

        cherryBloom = new Item("Cherry Bloom", "LIPSTICK", 30);
        rosePetal = new Item("Rose Petal", "LIPSTICK", 30);
        blusherBrush = new Item("Blusher Brush", "TOOL", 50);
        eyelashCurler = new Item("Eyelash curler", "TOOL", 100);
        wildRose = new Item("Wild Rose", "PURFUME", 200);
        cocoaButter = new Item("Cocoa Butter", "SKIN_CREAM", 250);

        nordstan.addStockedItems(cherryBloom, rosePetal, blusherBrush, eyelashCurler, wildRose, cocoaButter);
        backaplan.addStockedItems(cherryBloom, rosePetal, eyelashCurler, wildRose, cocoaButter);

        // Store events add themselves to the stocked items at their store
        masterclass = new StoreEvent("Eyeshadow Masterclass", nordstan);
        makeoverNordstan = new StoreEvent("Makeover", nordstan);
        makeoverBackaplan = new StoreEvent("Makeover", backaplan);
    }

    /**
     * Test Fall: happy path
     * - DRONE_DELIVERY selected
     * - weight is 400
     * - pickup is a store
     * - new store supports DRONE
     * - address is set
     * - location is NEARBY
     * - new store supports DRONE
     * => update the pickupLocation to the new store
     */
    @Test
    public void switchDroneDeliveryToAnotherDroneDeliveryStore() throws Exception {
        DeliveryInformation deliveryInfo = new DeliveryInformation("DRONE", nordstan, 60);
        deliveryInfo.setDeliveryAddress("NEARBY");

        Cart cart = new Cart();
        cart.addItem(cherryBloom);

        OnlineShopping shopping = new OnlineShopping(new Session());

        shopping.switchTo(backaplan, cart, deliveryInfo, nordstan, locationService);

        assertEquals("DRONE", deliveryInfo.getType());
        assertEquals("Backaplan", deliveryInfo.getPickupLocation().getName());
    }

    @Test
    @Disabled("general test WIP")
    public void switchStore() throws Exception {
        DeliveryInformation deliveryInfo = new DeliveryInformation("HOME_DELIVERY", nordstan, 60);
        deliveryInfo.setDeliveryAddress("NEARBY");

        Cart cart = new Cart();
        cart.addItem(cherryBloom);
        cart.addItem(blusherBrush);
        cart.addItem(masterclass);
        cart.addItem(makeoverNordstan);

        Session session = new Session();
        session.put("STORE", nordstan);
        session.put("DELIVERY_INFO", deliveryInfo);
        session.put("CART", cart);
        OnlineShopping shopping = new OnlineShopping(session);

        // TODO: make this test work
        // shopping.switchStore(backaplan);
        // assertEquals("DRONE", ((DeliveryInformation)session.get("DELIVERY_INFO")).getType());

    }


    /*
     * The central warehouse does not support Drone delivery
     * - valid drone delivery
     * => set to SHIPPING
     */

}
