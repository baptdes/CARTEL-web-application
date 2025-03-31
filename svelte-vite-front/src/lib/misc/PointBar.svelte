<script>
    // Props for customization
    export let Color = 'var(--primary)';    // Color of the horizontal bar
    export let barHeight = '2px';    // Height/thickness of the bar
    export let dotSize = '8px';      // Size of the dot
    export let width = '100%';       // Width of the entire component
    export let showStartDot = true;  // Option to show/hide the start dot
    export let showEndDot = true;    // Option to show/hide the end dot
</script>

<div class="point-bar-container" style="width: {width}">
    {#if showStartDot}
    <div 
        class="dot start-dot" 
        style="
            background-color: {Color}; 
            width: {dotSize}; 
            height: {dotSize}; 
            left: calc(-{dotSize}/2);
        "
    ></div>
    {/if}
    
    <div 
        class="bar" 
        style="background-color: {Color}; height: {barHeight};"
    ></div>
    
    {#if showEndDot}
    <div 
        class="dot end-dot" 
        style="
            background-color: {Color}; 
            width: {dotSize}; 
            height: {dotSize}; 
            right: calc(-{dotSize}/2);
        "
    ></div>
    {/if}
</div>

<style>
    .point-bar-container {
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
    }
</style>