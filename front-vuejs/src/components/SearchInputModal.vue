<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{
  open: boolean
  title: string
  placeholder: string
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', value: string): void
}>()

const value = ref('')

watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      value.value = ''
    }
  }
)

const canSubmit = computed(() => value.value.trim().length > 0 && !props.loading)

function submit() {
  if (!canSubmit.value) {
    return
  }
  emit('submit', value.value.trim())
}
</script>

<template>
  <div v-if="open" class="overlay" @click.self="emit('close')">
    <div class="modal">
      <h3>{{ title }}</h3>
      <div class="field">
        <input
          v-model="value"
          :placeholder="placeholder"
          type="text"
          @keyup.enter="submit"
        />
      </div>
      <div class="actions">
        <button class="btn secondary" @click="emit('close')">Annuler</button>
        <button class="btn primary" :disabled="!canSubmit" @click="submit">
          {{ loading ? 'Recherche...' : 'Valider' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(54, 34, 17, 0.42);
  display: grid;
  place-items: center;
  z-index: 50;
  padding: 16px;
}

.modal {
  width: min(460px, calc(100vw - 32px));
  box-sizing: border-box;
  background: #fff4e4;
  border: 1px solid #c9ab82;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 18px 42px rgba(50, 30, 15, 0.25);
  overflow: hidden;
}

.modal h3 {
  margin: 0 0 10px;
  color: #5a3a20;
}

.field {
  width: 100%;
}

input {
  display: block;
  width: 100%;
  min-width: 0;
  box-sizing: border-box;
  border: 1px solid #c9ab82;
  border-radius: 10px;
  padding: 11px 12px;
  background: #fff9f0;
  color: #4b2f1a;
  font-size: 1rem;
}

input:focus {
  outline: 2px solid #b3844d;
  outline-offset: 1px;
}

.actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 9px 14px;
  font-weight: 600;
  cursor: pointer;
}

.primary {
  background: #7d5331;
  color: #fff4e4;
}

.secondary {
  background: #e8d2b2;
  color: #4b2f1a;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
