<template>
  <Message
    severity="info"
    class="text-center"
    :pt="{ transition: { name: '' } }"
  >
    <slot />
  </Message>
</template>
