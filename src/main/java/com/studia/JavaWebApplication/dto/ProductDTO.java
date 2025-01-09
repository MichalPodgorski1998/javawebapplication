package com.studia.JavaWebApplication.dto;

import com.studia.JavaWebApplication.model.Artist;
import com.studia.JavaWebApplication.model.MediaType;
import com.studia.JavaWebApplication.model.MusicCategory;
import com.studia.JavaWebApplication.validate.UniqueImage;

import jakarta.validation.constraints.*;
import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UniqueImage
//@UniqueImage(message = "Ten sam obraz jest już przypisany do innego produktu.")
@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Tytuł jest wymagany.")
    @Size(min = 2, max = 255, message = "Tytuł musi zawierać od 3 do 255 znaków.")
    private String title;

    @NotBlank(message = "Opis jest wymagany.")
    @Size(min = 10, max = 1800, message = "Opis musi zawierać od 10 do 1000 znaków.")
    private String description;

    @NotNull(message = "Data wydania jest wymagana.")
    @PastOrPresent(message = "Data wydania nie może być w przyszłości.")
    private LocalDate releaseDate;

    @NotNull(message = "Cena jest wymagana.")
    @DecimalMin(value = "0.01", message = "Cena musi być większa niż 0.")
    @DecimalMax(value = "10000.00", message = "Cena nie może przekraczać 10,000.")
    private BigDecimal price;

    @Min(value = 0, message = "Stan magazynowy nie może być ujemny.")
    @Max(value = 10000, message = "Stan magazynowy nie może przekraczać 10,000.")
    private int stockQuantity;

    @NotNull(message = "Kategoria muzyczna jest wymagana.")
    private MusicCategory musicCategory;

    private LocalDateTime addedDateTime;

    @NotNull(message = "Rodzaj nośnika jest wymagany.")
    private MediaType mediaType;

    @NotNull(message = "Artysta jest wymagany.")
    private Artist artist;

    private String image;
    private MultipartFile imageProduct;
    private String imageUrl;
    private String existingImage; // Zakodowany obraz w Base64

    // Gettery i settery

    public ProductDTO() {}

    public ProductDTO(Long id, String title, String description, LocalDate releaseDate, BigDecimal price, int stockQuantity, MusicCategory musicCategory, LocalDateTime addedDateTime, MediaType mediaType, Artist artist, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.musicCategory = musicCategory;
        this.addedDateTime = addedDateTime;
        this.mediaType = mediaType;
        this.artist = artist;
        this.image = image;
    }

    public MultipartFile getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(MultipartFile imageProduct) {
        this.imageProduct = imageProduct;
    }

    public String getExistingImage() {
        return existingImage;
    }

    public void setExistingImage(String existingImage) {
        this.existingImage = existingImage;
    }

    public String getImageUrl() {
        return image != null
                ? "data:image/jpeg;base64," + image
                : "https://demofree.sirv.com/nope-not-here.jpg";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public MusicCategory getMusicCategory() {
        return musicCategory;
    }

    public void setMusicCategory(MusicCategory musicCategory) {
        this.musicCategory = musicCategory;
    }

    public LocalDateTime getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(LocalDateTime addedDateTime) {
        this.addedDateTime = addedDateTime;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
