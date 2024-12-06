package com.studia.JavaWebApplication.dto;

import com.studia.JavaWebApplication.model.Artist;
import com.studia.JavaWebApplication.model.MediaType;
import com.studia.JavaWebApplication.model.MusicCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private BigDecimal price;
    private int stockQuantity;
    private MusicCategory musicCategory;
    private LocalDateTime addedDateTime;
    private MediaType mediaType;
    private Artist artist;

    public ProductDTO() {}

    public ProductDTO(Long id, String title, String description, LocalDate releaseDate, BigDecimal price, int stockQuantity, MusicCategory musicCategory, LocalDateTime addedDateTime, MediaType mediaType, Artist artist) {
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
}
