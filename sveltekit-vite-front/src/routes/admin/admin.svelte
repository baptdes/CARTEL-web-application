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

<style>
    .Catalogue{
        background: white;
        height:10em;
        width:40em;
    }

    .Course{
      background: white;
      height:10em;
      width:40em;
    }

    .Tresorerie{
      background: white;
      height:10em;
      width:40em;
    }

    .Empruntpret{
      background: white;
      height:10em;
      width:40em;
    }

    .bouton{
      border-radius: 0.5em;
      display: block;
      padding:0em;
    }

    .Thebouton{
      background: rgba(255,255,255,0);
      color :red;
      font-size: 2em;
      height:10em;
      width:40em;
    }

    .minbox{
      padding: 1em;
    }

    .ext{
      padding: 0em;
    }
    
    .boxes{
      padding: 0em;
      display: flex;
    }
 
</style>
