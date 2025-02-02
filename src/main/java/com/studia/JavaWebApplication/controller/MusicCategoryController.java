package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.model.MusicCategory;
import com.studia.JavaWebApplication.service.MusicCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MusicCategoryController {
    @Autowired
    private MusicCategoryService musicCategoryService;

    @GetMapping("/products/musicCategories")
    public String categories(Model model) {
        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();

        model.addAttribute("musicCategories", musicCategories);
        model.addAttribute("size", musicCategories.size());
        model.addAttribute("musicCategoryNew", new MusicCategoryDTO());
        model.addAttribute("musicCategoryEdit", new MusicCategoryDTO());
        return "products/musicCategories";
    }
    @PostMapping("/products/saveMusicCategory")
    public String saveMusicCategory(@Valid @ModelAttribute("musicCategoryNew") MusicCategoryDTO musicCategoryDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("validationErrors", errorMessages);
            return "redirect:/products/musicCategories";
        }

        try {
            MusicCategory musicCategory = new MusicCategory();
            musicCategory.setName(musicCategoryDTO.getName());
            musicCategoryService.save(musicCategory);
            redirectAttributes.addFlashAttribute("success", "Kategoria muzyczna została dodana pomyślnie");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("failed", "Kategoria muzyczna już istnieje w bazie danych");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Error z jakiegoś powodu nie można dodać kategorii muzyczne, spróbuj ponownie później");
        }
        return "redirect:/products/musicCategories";
    }
    @PostMapping("/products/updateMusicCategory")
    public String updateMusicCategory(@Valid @ModelAttribute("musicCategoryEdit") MusicCategoryDTO musicCategoryDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("validationErrors", errorMessages);
            return "redirect:/products/musicCategories";
        }
        try {
            MusicCategory musicCategory = new MusicCategory();
            musicCategory.setId(musicCategoryDTO.getId());
            musicCategory.setName(musicCategoryDTO.getName());
            musicCategoryService.update(musicCategory);
            redirectAttributes.addFlashAttribute("success", "Kategoria muzyczna zaktualizowana pomyśłnie");
        }
        catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("failed", "Kategoria muzyczna juz istnieje w bazie danych");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Wystąpił błąd podczas aktualizacji kategorii muzycznej");
        }
        return "redirect:/products/musicCategories";
    }

    @DeleteMapping("/delete-musicCategory")
    @ResponseBody
    public ResponseEntity<String> deleteMusicCategory(@RequestParam("id") Long id) {
        try {
            musicCategoryService.deleteById(id);
            return ResponseEntity.ok("Kategoria muzyczna została pomyślnie usunięta.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nie można usunąć kategorii muzycznej, ponieważ są z nim powiązane inne produkty.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił błąd podczas usuwania kategorii muzycznej.");
        }
    }

}
