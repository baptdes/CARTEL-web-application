<script>
  import { createEventDispatcher } from 'svelte';
  
  const dispatch = createEventDispatcher();
  
  // Props
  let { show = false, title = 'Confirmation', message = 'Êtes-vous sûr ?', cancelText = 'Annuler', confirmText = 'Confirmer' } = $props();
  
  // Close the dialog and emit 'cancel' event
  function handleCancel() {
    dispatch('cancel');
  }
  
  // Close the dialog and emit 'confirm' event
  function handleConfirm() {
    dispatch('confirm');
  }
  
  // Handle keyboard events (close on Escape)
  function handleKeydown(event) {
    if (event.key === 'Escape') {
      handleCancel();
    }
  }
</script>

{#if show}
  <div 
    class="dialog-backdrop" 
    on:click={handleCancel}
    on:keydown={handleKeydown}
    role="dialog" 
    aria-modal="true"
    tabindex="-1"
  >
    <div class="dialog-content" on:click|stopPropagation>
      <div class="dialog-header">
        <h3>{title}</h3>
      </div>
      <div class="dialog-body">
        <p>{message}</p>
      </div>
      <div class="dialog-footer">
        <button class="cancel-btn" on:click={handleCancel}>{cancelText}</button>
        <button class="confirm-btn" on:click={handleConfirm}>{confirmText}</button>
      </div>
    </div>
  </div>
{/if}

<style lang="scss">
  .dialog-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .dialog-content {
    background-color: var(--back);
    border: 1px solid var(--secondary);
    max-width: 400px;
    width: 90%;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  }
  
  .dialog-header {
    padding: 1rem 1.5rem;
    background-color: var(--tertiary);
    border-bottom: 1px solid var(--secondary);
    
    h3 {
      color: var(--primary);
      margin: 0;
      font-size: 1.25rem;
    }
  }
  
  .dialog-body {
    padding: 1.5rem;
    
    p {
      color: var(--primary);
      margin: 0;
      line-height: 1.5;
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    padding: 1rem 1.5rem;
    border-top: 1px solid var(--secondary);
    
    button {
      padding: 0.6rem 1.2rem;
      cursor: pointer;
      border-radius: 3px;
      font-weight: bold;
    }
    
    .cancel-btn {
      background-color: transparent;
      color: var(--primary);
      border: 1px solid var(--secondary);
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.05);
      }
    }
    
    .confirm-btn {
      background-color: var(--accent);
      color: white;
      border: none;
      
      &:hover {
        filter: brightness(0.9);
      }
    }
  }
</style>
