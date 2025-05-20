<script>
    // Props for customization  
    export let Color = 'var(--primary)';    
    export let barHeight = '2px';    
    export let dotSize = '8px';      
    export let width = '100%';       
    export let showStartDot = true;  
    export let showEndDot = true;    
    export let startDotSrc = null;   
    export let endDotSrc = null;     
    export let imageScale = 1;
    export let startImageScale = {imageScale};  
    export let endImageScale = {imageScale};       
</script>

<div class="point-bar-container" style="width: {width};">
    {#if showStartDot}
    <div 
        class="dot start-dot" 
        style="
            width: {dotSize}; 
            height: {dotSize}; 
            left: calc(-{dotSize}/2);
            {startDotSrc ? '' : `background-color: ${Color};`}
        "
    >
        {#if startDotSrc}
            <div class="img-container" style="--scale: {startImageScale}">
                <img src={startDotSrc} alt="start dot" class="dot-image" />
            </div>
        {/if}
    </div>
    {/if}
    
    <div 
        class="bar" 
        style="background-color: {Color}; height: {barHeight};"
    ></div>
    
    {#if showEndDot}
    <div 
        class="dot end-dot" 
        style="
            width: {dotSize}; 
            height: {dotSize}; 
            right: calc(-{dotSize}/2);
            {endDotSrc ? '' : `background-color: ${Color};`}
        "
    >
        {#if endDotSrc}
            <div class="img-container" style="--scale: {endImageScale}">
                <img src={endDotSrc} alt="end dot" class="dot-image" />
            </div>
        {/if}
    </div>
    {/if}
</div>

<style>
    .point-bar-container {
        align-self: center;
        justify-self: center;
        position: relative;
        display: block;
        margin: 1rem 0;
    }

    .bar {
        width: 100%;
    }

    .dot {
        position: absolute;
        border-radius: 50%;
        top: 50%;
        transform: translateY(-50%);
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .img-container {
        position: relative;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        transform: scale(var(--scale, 1));
    }
    
    .dot-image {
        max-width: 100%;
        max-height: 100%;
        width: auto;
        height: auto;
    }
</style>