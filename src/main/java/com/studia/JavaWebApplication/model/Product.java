package com.studia.JavaWebApplication.model;

import com.studia.JavaWebApplication.dto.ProductDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private BigDecimal price;
    private int stockQuantity;
    private LocalDateTime addedDateTime;
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "music_category_id", referencedColumnName = "music_category_id")
//    private MusicCategory musicCategory;
//
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "artist_id", referencedColumnName = "artist_id")
//    private Artist artist;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "music_category_id", referencedColumnName = "music_category_id", nullable = true)
    private MusicCategory musicCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id", referencedColumnName = "artist_id", nullable = true)
    private Artist artist;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;

    public Product() {
    }

    public Product(String title, String description, LocalDate releaseDate, BigDecimal price, int stockQuantity, MusicCategory musicCategory, MediaType mediaType, Artist artist, String image) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.musicCategory = musicCategory;
        this.addedDateTime = LocalDateTime.now();
        this.mediaType = mediaType;
        this.artist = artist;
        this.image = image;
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
