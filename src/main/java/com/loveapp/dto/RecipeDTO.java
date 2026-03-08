package com.loveapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class RecipeDTO {
    
    private Long id;
    
    private String name;
    
    private String category;
    
    private Integer difficulty;
    
    private Integer cookTime;
    
    private String coverImage;
    
    private String ingredients;
    
    private String steps;
    
    private String tips;
    
    private Integer tryCount;
    
    private LocalDate lastTryDate;
    
    private Boolean isFavorite;
    
    private Boolean isOfficial;
}
