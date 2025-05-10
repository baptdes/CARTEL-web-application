<script>
    import { goto } from '$app/navigation';
  import { adminPageState } from './store.js';
</script>

<main>
  <div class="boxes">
    <div class="ext">
      <div class="minbox">
        <div class="Catalogue bouton">
          <button class="Thebouton" type="button" onclick={()=>goto('/admin/catalogue')}><center>Catalogue</center></button>
        </div>
      </div>
      <div class="minbox">
        <div class="Course bouton">
          <button class="Thebouton" type="button" onclick={()=>goto('/admin/purge_course')}>Purger Course </button>
        </div>
      </div>
    </div>

    <div class="ext">
      <div class="minbox">
        <div class="Tresorerie bouton">
          <button class="Thebouton" type="button" onclick={()=>goto('/admin/tresorerie')}>Trésorerie</button>
        </div>
      </div>
      <div class="minbox">
        <div class="Empruntpret bouton">
          <button class="Thebouton" type="button" onclick={()=>goto('/admin/emprunt_pret')}>Emprunt/prêt </button>
        </div>
      </div>
    </div>
  </div>
</main>

<style>
  .Catalogue,
  .Course,
  .Tresorerie,
  .Empruntpret {
    background: white;
    height: 10em;
    width: 100%;
    display: flex; 
    align-items: center; 
    justify-content: center; 
  }

    .bouton{
      border-radius: 0.5em;
      display: block;
      padding:0em;
    }

    .Thebouton{
      background: rgba(255,255,255,0);
      color :red;
      height: 100%; 
      width: 100%; 
      border: none; 
      display: flex; 
      align-items: center; 
      justify-content: center; 
      cursor: pointer;
      font-size:1.5rem;
      font-weight: bold;
      font-family: "Pirata One", cursive;
  }

    .minbox{
      padding: 1em;
    }

    .ext{
      padding: 0em;
    }
    
    .boxes {
      display: grid; 
      grid-template-columns: repeat(2, 1fr); 
      gap: 1em; 
      padding: 0;
  }
 
</style>
