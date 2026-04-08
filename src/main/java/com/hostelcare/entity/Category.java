package com.hostelcare.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a complaint category (e.g., Plumbing, Electrical, Cleanliness).
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
