package ru.practicum.ewm.main.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.model.CommentSortByAdmin;
import ru.practicum.ewm.main.comment.service.CommentService;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.compilation.service.CompilationService;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventUpdateDto;
import ru.practicum.ewm.main.event.model.EventSearchCriteriaByAdmin;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CommentService commentService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategory = categoryService.addCategory(categoryDto);
        log.info("Category {id={},name={}} has been got", savedCategory.getId(), savedCategory.getName());
        return savedCategory;
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto);
        log.info("Category {id={},name={}} has been updated", updatedCategory.getId(), updatedCategory.getName());
        return updatedCategory;
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
        log.info("Category with id={} has been deleted", catId);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<UserDto> users = userService.getUsersByIds(ids, from, size);
        log.info("User list with size = {} has been got", users.size());
        return users;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        UserDto addedUser = userService.addUser(userDto);
        log.info("User {id={},name={},email={}} has been added",
                userDto.getId(), userDto.getName(), userDto.getEmail());
        return addedUser;
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("User with id={} has been deleted", userId);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid EventUpdateDto eventDto) {
        EventFullDto updatedEvent = eventService.updateEventByAdmin(eventDto, eventId);
        log.info("Event with id={} updated by admin", eventId);
        return updatedEvent;
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEventsByAdmin(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size,
                                                  @Valid @ModelAttribute EventSearchCriteriaByAdmin criteria
    ) {
        Pageable pageable = PageRequest.of(from, size);
        List<EventFullDto> events = eventService.searchEventsByAdmin(pageable, criteria);
        log.info("Event list with size={} has been got", events.size());
        return events;
    }

    @PostMapping("/compilations")
    @ResponseStatus(CREATED)
    public CompilationDto addCompilations(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        CompilationDto compilation = compilationService.addCompilation(newCompilationDto);
        log.info("Compilation with id={} has been added", compilation.getId());
        return compilation;
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilations(@PathVariable Long compId,
                                             @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        CompilationDto updatedCompilation = compilationService.updateCompilation(updateCompilationRequest, compId);
        log.info("Compilation with id={} has been updated", compId);
        return updatedCompilation;
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilations(@PathVariable Long compId) {
        log.info("compilation with id={} has been deleted", compId);
        compilationService.deleteCompilationById(compId);
    }

    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> searchComments(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size,
                                           @RequestParam(required = false) LocalDateTime rangeStart,
                                           @RequestParam(required = false) LocalDateTime rangeEnd,
                                           @RequestParam(required = false) Set<Long> userIds,
                                           @RequestParam(required = false) Set<Long> eventIds,
                                           @RequestParam(required = false) String text,
                                           @RequestParam(required = false) CommentSortByAdmin sort) {

        List<CommentDto> comments = commentService.getCommentsByAdmin(from, size, rangeStart, rangeEnd, userIds,
                eventIds, text, sort);
        log.info("Comments with size={} has been got by admin", comments.size());
        return comments;
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void delCommentByAdmin(@PathVariable Long commentId) {
        log.info("Comment with id={} deleted by admin", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}