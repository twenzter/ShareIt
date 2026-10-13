//package ru.yandex.practicum.item;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.exception.NotFoundException;
//import ru.yandex.practicum.item.dto.ItemDto;
//import ru.yandex.practicum.item.dto.ItemUpdateRequest;
//import ru.yandex.practicum.user.*;
//
//import java.util.List;
//
//public class ItemServiceTest {
//    ItemRepository itemRepository;
//    UserRepository userRepository;
//    ItemService itemService = new ItemServiceImpl(itemRepository, userRepository);
//    UserService userService = new UserServiceImpl(userRepository);
//
//    @BeforeEach
//    void beforeEach() {
//        User user1 =  new User(1L, "email@mail.ru", "name");
//        userService.saveUser(user1);
//    }
//
//    @Test
//    void getAllItemsTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        ItemDto item2 = getItemDto(2L, "item2", "desc2");
//        itemService.addNewItem(1L, item1);
//        itemService.addNewItem(1L, item2);
//        assertThat(itemService.getItems(1L)).isEqualTo(List.of(item1, item2));
//    }
//
//    @Test
//    void getAllItemsWithZeroResultsTest() {
//        assertThat(itemService.getItems(1L)).isEqualTo(List.of());
//    }
//
//    @Test
//    void getItemsWithUnknownUserIdTest() {
//        try {
//            itemService.getItems(2L);
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//
//    }
//
//    @Test
//    void addNewItemTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        assertThat(itemService.getItems(1L)).isEqualTo(List.of(item1));
//    }
//
//    @Test
//    void addNewItemWithUnknownUserIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        try {
//            itemService.addNewItem(2L, item1);
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//    }
//
//    @Test
//    void deleteItemTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        itemService.deleteItem(1L, 1L);
//        assertThat(itemService.getItems(1L)).isEqualTo(List.of());
//    }
//
//    @Test
//    void deleteItemWithUnknownUserIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.deleteItem(2L, 1L);
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//    }
//
//    @Test
//    void getItemByIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        assertThat(itemService.getItemById(1L, 1L)).isEqualTo(item1);
//    }
//
//    @Test
//    void getItemByIdWithUnknownUserIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.getItemById(2L, 1L);
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//    }
//
//    @Test
//    void getItemByIdWithUnknownItemIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.getItemById(1L, 2L);
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("item with id 2 was not found");
//        }
//    }
//
//    @Test
//    void updateItemTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        itemService.updateItem(1L, 1L,
//                new ItemUpdateRequest("updName", "updDesc", null));
//        ItemDto updItem = getItemDto(1L, "updName", "updDesc");
//        assertThat(itemService.getItems(1L)).isEqualTo(List.of(updItem));
//    }
//
//    @Test
//    void updateItemWithUnknownUserIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.updateItem(2L, 1L,
//                    new ItemUpdateRequest("updName", "updDesc", null));
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//    }
//
//    @Test
//    void updateItemWithUnknownItemIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.updateItem(1L, 2L,
//                    new ItemUpdateRequest("updName", "updDesc", null));
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("item with id 2 was not found");
//        }
//    }
//
//
//    @Test
//    void searchWithOneResultByNameTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        assertThat(itemService.search(1L, "item1")).isEqualTo(List.of(item1));
//    }
//
//    @Test
//    void searchWithOneResultByDescriptionTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        assertThat(itemService.search(1L, "desc1")).isEqualTo(List.of(item1));
//    }
//
//    @Test
//    void searchWithZeroResultsTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        assertThat(itemService.search(1L, "item234")).isEqualTo(List.of());
//    }
//
//    @Test
//    void searchWithUnknownItemIdTest() {
//        ItemDto item1 = getItemDto(1L, "item1", "desc1");
//        itemService.addNewItem(1L, item1);
//        try {
//            itemService.search(2L, "item234");
//            Assertions.fail();
//        } catch (NotFoundException e) {
//            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
//        }
//    }
//
//    ItemDto getItemDto(Long id, String name, String description) {
//        return new ItemDto(id, name, description, true, null);
//    }
//}
