package cartel.spring_boot_api.model;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "boardId")
public class BoardGame extends CatalogItem {
    
    private String designer;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer minAge;
    private Integer playingTime; // in minutes
    private Boolean hasAllPieces;
    
    public String getDesigner() {
        return designer;
    }
    
    public void setDesigner(String designer) {
        this.designer = designer;
    }
    
    public Integer getMinPlayers() {
        return minPlayers;
    }
    
    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }
    
    public Integer getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public Integer getMinAge() {
        return minAge;
    }
    
    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }
    
    public Integer getPlayingTime() {
        return playingTime;
    }
    
    public void setPlayingTime(Integer playingTime) {
        this.playingTime = playingTime;
    }
    
    public Boolean getHasAllPieces() {
        return hasAllPieces;
    }
    
    public void setHasAllPieces(Boolean hasAllPieces) {
        this.hasAllPieces = hasAllPieces;
    }
}
