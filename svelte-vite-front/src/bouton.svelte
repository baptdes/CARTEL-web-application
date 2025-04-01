<script>
    export let name;
    export let img='./assets/LEGO_logo.png';
	
</script>

<div class="Thebackground" style="background-image: url(${img});">
    <button class="Thebouton" type="button">{name}</button>
</div>


<style>
    .Thebackground{
        display: block;
    }

    .Thebouton{
        background: rgba(255,255,255,0);
        
    }
</style>