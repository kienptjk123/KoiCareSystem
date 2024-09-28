package com.swpproject.koi_care_system.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "description_detail", nullable = false, columnDefinition = "TEXT")
    private String description_detail;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Column(name = "rating")
    private Double rating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks;

    private Double calculateAverageRating() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        double sum = feedbacks.stream().mapToInt(Feedback::getStar).sum();
        double average = sum / feedbacks.size();
        return Math.round(average * 2) / 2.0;
    }
    @PrePersist
    @PreUpdate
    public void updateRating() {
        this.rating = this.calculateAverageRating();
    }
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="issue_id")
    private Issue issue;

    @ManyToMany
    @JoinTable(
            name = "product_promotion",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private Set<Promotion> promotions = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="supplier_id")
    private Supplier supplier;
    public Product(String name, String brand, BigDecimal price, int inventory, String description,String description_detail, Category category, Supplier supplier) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.description = description;
        this.description_detail=description_detail;
        this.category = category;
        this.supplier = supplier;
    }
}
