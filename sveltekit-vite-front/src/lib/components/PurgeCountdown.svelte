<script>
  // Props with default values
  let { targetDate = null } = $props();
  
  // Countdown state
  let days = $state(0);
  let hours = $state(0);
  let minutes = $state(0);
  let seconds = $state(0);
  let isValidFutureDate = $state(false);
  
  // Set the date of the next purge if not provided
  let purgeDate = $state(null);
  
  $effect(() => {
    if (targetDate instanceof Date) {
      // Use the Date object directly
      purgeDate = targetDate;
    } else if (typeof targetDate === 'string') {
      // Parse the date string
      purgeDate = new Date(targetDate);
    } else {
      // Default: no date specified
      purgeDate = null;
    }
    
    // Check if the date is valid and in the future
    const now = new Date();
    isValidFutureDate = purgeDate && !isNaN(purgeDate) && purgeDate > now;
    
    if (isValidFutureDate) {
      updateCountdown();
    }
  });

  function updateCountdown() {
    if (!isValidFutureDate) return;
    
    const now = new Date();
    const difference = purgeDate - now;
    
    if (difference > 0) {
      days = Math.floor(difference / (1000 * 60 * 60 * 24));
      hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
      seconds = Math.floor((difference % (1000 * 60)) / 1000);
      isValidFutureDate = true;
    } else {
      // Date has passed
      isValidFutureDate = false;
    }
  }
  
  // Start countdown timer
  let interval;
  $effect(() => {
    interval = setInterval(updateCountdown, 1000);
    
    return () => clearInterval(interval);
  });
  
  // Fun messages for when there's no valid future date
  const funnyMessages = [
    "Par les écailles du grand dragon 🐉 ! La purge est déjà passée !",
    "La purge est comme une ombre dans la nuit... introuvable !",
    "Les dragons 🐉 ont déjà tout brûlé ! Il faut planifier la prochaine purge.",
    "Chronos a mangé notre calendrier ! Aidez-nous à en définir un nouveau."
  ];
  
  // Randomly select one message
  const randomMessage = funnyMessages[Math.floor(Math.random() * funnyMessages.length)];
</script>

{#if isValidFutureDate}
  <div class="countdown-timer">
    <div class="countdown-box">
      <span class="number">{days}</span>
      <span class="label">Jours</span>
    </div>
    <div class="countdown-box">
      <span class="number">{hours}</span>
      <span class="label">Heures</span>
    </div>
    <div class="countdown-box">
      <span class="number">{minutes}</span>
      <span class="label">Minutes</span>
    </div>
    <div class="countdown-box">
      <span class="number">{seconds}</span>
      <span class="label">Secondes</span>
    </div>
  </div>
{:else}
  <div class="no-date-message">
    <div class="message-box">
      <span class="message">{randomMessage}</span>
    </div>
  </div>
{/if}

<style>
  .countdown-timer {
    display: flex;
    justify-content: center;
    gap: 1.5rem;
  }
  
  .countdown-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 80px;
  }
  
  .number {
    font-family: "Pirata One", cursive;
    font-size: 3.5rem;
    color: #fff;
    background: var(--dark-red);
    padding: 0.5rem 1rem;
    border-radius: 5px;
    display: block;
    min-width: 80px;
    text-align: center;
  }
  
  .label {
    margin-top: 0.5rem;
    font-size: 1rem;
    color: var(--white);
    text-transform: uppercase;
    letter-spacing: 1px;
  }
  
  .no-date-message {
    display: flex;
    justify-content: center;
    padding: 2rem;
  }
  
  .message-box {
    background: var(--dark-red);
    padding: 1.5rem;
    border-radius: 8px;
    max-width: 80%;
    position: relative;
    animation: float 3s ease-in-out infinite;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  }
  
  .message {
    font-family: "Pirata One", cursive;
    font-size: 2rem;
    color: #fff;
    text-align: center;
    display: block;
  }
  
  @keyframes float {
    0% { transform: translateY(0px); }
    50% { transform: translateY(-10px); }
    100% { transform: translateY(0px); }
  }

  @keyframes bounce {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-15px); }
  }
  
  @media (max-width: 768px) {
    .countdown-timer {
      flex-wrap: wrap;
    }
    
    .countdown-box {
      min-width: 70px;
    }
    
    .number {
      font-size: 2.5rem;
      min-width: 60px;
    }
    
    .message {
      font-size: 1.5rem;
    }
  }
</style>
