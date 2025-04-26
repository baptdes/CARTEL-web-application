<script>
  import { adminPageState } from './store.js';
</script>

<main>
  <div class="boxes">
    <div class="ext">
      <div class="minbox">
        <div class="Catalogue bouton">
          <button class="Thebouton" type="button" onclick={()=>$adminPageState=1}><center>Catalogue</center></button>
        </div>
      </div>
      <div class="minbox">
        <div class="Course bouton">
          <button class="Thebouton" type="button" onclick={()=>$adminPageState=2}>Purger Course </button>
        </div>
      </div>
    </div>

    <div class="ext">
      <div class="minbox">
        <div class="Tresorerie bouton">
          <button class="Thebouton" type="button" onclick={()=>$adminPageState=3}>Trésorerie</button>
        </div>
      </div>
      <div class="minbox">
        <div class="Empruntpret bouton">
          <button class="Thebouton" type="button" onclick={()=>$adminPageState=4}>Emprunt/prêt </button>
        </div>
      </div>
    </div>
  </div>
</main>

<style lang="scss">
  $button-height: 10em;
  $button-width: 40em;
  $border-radius: 0.5em;
  
  .bouton {
    border-radius: $border-radius;
    display: block;
    padding: 0;
    
    &.Catalogue, 
    &.Course, 
    &.Tresorerie,
    &.Empruntpret {
      background: white;
      height: $button-height;
      width: $button-width;
    }
  }

  .Thebouton {
    background: rgba(255, 255, 255, 0);
    color: red;
    font-size: 2em;
    height: $button-height;
    width: $button-width;
  }

  .minbox {
    padding: 1em;
  }

  .ext {
    padding: 0;
  }
  
  .boxes {
    padding: 0;
    display: flex;
  }
</style>
