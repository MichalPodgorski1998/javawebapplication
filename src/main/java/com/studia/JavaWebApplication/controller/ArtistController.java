package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.ArtistDto;
import com.studia.JavaWebApplication.model.Artist;
import com.studia.JavaWebApplication.service.ArtistService;
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
public class ArtistController {
    @Autowired
    private ArtistService artistService;

    @GetMapping("/products/artists")
    public String listArtists(Model model) {
        List<ArtistDto> artists = artistService.findAllArtists();
        model.addAttribute("artists", artists);
        model.addAttribute("size", artists.size());
        model.addAttribute("artistNew", new Artist());
        model.addAttribute("artistEdit", new Artist());
        return "products/artists";
    }

    @PostMapping("/artists/save")
    public String saveArtist(@Valid @ModelAttribute("artistNew") ArtistDto artistDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("validationErrors", errorMessages);
            return "redirect:/products/artists";
        }
        try {
            Artist artist = new Artist();
            artist.setName(artistDto.getName());
            artistService.save(artist);
            redirectAttributes.addFlashAttribute("success", "Artysta dodany pomyślnie");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("failed", "Artysta o tym pseudnimie już istnieje w bazie danych");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Error z jakiegoś powodu nie można dodać artsty, spróbuj ponownie później");
        }

        return "redirect:/products/artists";
    }
    @PostMapping("/artists/update")
    public String updateArtist(
            @Valid @ModelAttribute("artistEdit") ArtistDto artistDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("validationErrors", errorMessages);
            return "redirect:/products/artists";
        }
        try {
            Artist artist = new Artist();
            artist.setId(artistDto.getId());
            artist.setName(artistDto.getName());
            artistService.update(artist);
            redirectAttributes.addFlashAttribute("success", "Artysta zaktualizowany pomyślnie");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("failed", "Artysta o tym pseudnimie już istnieje w bazie danych");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Wystąpił błąd podczas aktualizacji artysty");
        }
        return "redirect:/products/artists";
    }

//    @GetMapping("/artists/delete/{id}")
//    public String deleteArtist(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        try {
//            artistService.deleteById(id);
//            redirectAttributes.addFlashAttribute("success", "Artysta został pomyślnie usunięty.");
//        } catch (DataIntegrityViolationException e) {
//            redirectAttributes.addFlashAttribute("failed", "Nie można usunąć artysty, ponieważ są z nim powiązane inne produkty.");
//        }  catch (Exception e) {
//            redirectAttributes.addFlashAttribute("failed", "Wystąpił błąd podczas usuwania artysty.");
//        }
//        return "redirect:/products/artists";
//    }

    @DeleteMapping("/delete-artist")
    @ResponseBody
    public ResponseEntity<String> deleteArtist(@RequestParam("id") Long id) {
        try {
            artistService.deleteById(id);
            return ResponseEntity.ok("Artysta został pomyślnie usunięty.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nie można usunąć artysty, ponieważ są z nim powiązane inne produkty.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił błąd podczas usuwania artysty.");
        }
    }

}
