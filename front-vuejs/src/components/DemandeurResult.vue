<script setup lang="ts">
import { RouterLink } from 'vue-router'

interface VisaItem {
  id: number
  reference: string
  dateDebut: string | null
  dateFin: string | null
  categorie: string | null
}

interface DemandeItem {
  id: number
  dateDemande: string | null
  dateTraitement: string | null
  observations: string | null
  motifRejet: string | null
  statut: string | null
  typeDemande: string | null
  visa: VisaItem | null
}

interface HistoriqueItem {
  id: number
  idDemande: number | null
  statut: string | null
  dateChangement: string | null
  commentaire: string | null
}

interface ResponseModel {
  recherche: { type: string; valeur: string }
  demandeur: {
    id: number
    nom: string
    prenom: string
    dateNaissance: string | null
    lieuNaissance: string | null
    telephone: string | null
    email: string | null
    adresse: string | null
    situationFamiliale: string | null
    nationalite: string | null
  } | null
  details: {
    carteResident: {
      id: number
      numero: string
      dateDonnation: string | null
      dateExpiration: string | null
      etat: string | null
    } | null
    passeport: {
      id: number
      numeroPasseport: string
      dateDelivrance: string | null
      dateExpiration: string | null
      paysDelivrance: string | null
    } | null
    visa: VisaItem | null
  }
  demandes: DemandeItem[]
  historiques: HistoriqueItem[]
}

defineProps<{
  data: ResponseModel | null
}>()

function formatDate(value: string | null | undefined) {
  if (!value) return '-'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return new Intl.DateTimeFormat('fr-FR').format(date)
}

function formatDateTime(value: string | null | undefined) {
  if (!value) return '-'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return new Intl.DateTimeFormat('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}
</script>

<template>
  <section v-if="data" class="result-card">
    <div class="topbar">
      <div>
        <p class="eyebrow">Détails de la demande</p>
   
      </div>


    </div>

    <div class="grid two panels-row">
      <article class="panel highlight">
        <div class="panel-head">
          <h3>Informations demandeur</h3>
          <span class="chip">{{ data.recherche.type }}: {{ data.recherche.valeur }}</span>
        </div>

        <div class="info-grid">
          <div><strong>ID:</strong> {{ data.demandeur?.id ?? '-' }}</div>
          <div><strong>Nom:</strong> {{ data.demandeur?.nom || '-' }}</div>
          <div><strong>Prénom:</strong> {{ data.demandeur?.prenom || '-' }}</div>
          <div><strong>Date de naissance:</strong> {{ formatDate(data.demandeur?.dateNaissance) }}</div>
          <div><strong>Lieu de naissance:</strong> {{ data.demandeur?.lieuNaissance || '-' }}</div>
          <div><strong>Nationalité:</strong> {{ data.demandeur?.nationalite || '-' }}</div>
          <div><strong>Situation familiale:</strong> {{ data.demandeur?.situationFamiliale || '-' }}</div>
          <div><strong>Téléphone:</strong> {{ data.demandeur?.telephone || '-' }}</div>
          <div><strong>Email:</strong> {{ data.demandeur?.email || '-' }}</div>
          <div class="span-2"><strong>Adresse:</strong> {{ data.demandeur?.adresse || '-' }}</div>
        </div>
      </article>

      <article class="panel">
        <h3>Informations demande</h3>
        <div v-if="data.demandes.length" class="info-grid">
          <div><strong>ID demande:</strong> {{ data.demandes[0]?.id ?? '-' }}</div>
          <div><strong>Type:</strong> {{ data.demandes[0]?.typeDemande || '-' }}</div>
          <div><strong>Statut:</strong> {{ data.demandes[0]?.statut || '-' }}</div>
          <div><strong>Date de demande:</strong> {{ formatDate(data.demandes[0]?.dateDemande) }}</div>
        </div>
        <p v-else>Aucune demande trouvée.</p>
      </article>
    </div>

    <div class="grid two panels-row">
      <article class="panel accent-box">
        <h3>Carte résident</h3>
        <div class="info-grid">
          <div><strong>Numéro carte:</strong> {{ data.details.carteResident?.numero || '-' }}</div>
          <div><strong>Date donation:</strong> {{ formatDate(data.details.carteResident?.dateDonnation) }}</div>
          <div><strong>Date expiration:</strong> {{ formatDate(data.details.carteResident?.dateExpiration) }}</div>
          <div><strong>État:</strong> {{ data.details.carteResident?.etat || '-' }}</div>
        </div>
      </article>

      <article class="panel accent-box strong">
        <h3>Passeport</h3>
        <div class="info-grid">
          <div><strong>Numéro passeport:</strong> {{ data.details.passeport?.numeroPasseport || '-' }}</div>
          <div><strong>Date délivrance:</strong> {{ formatDate(data.details.passeport?.dateDelivrance) }}</div>
          <div><strong>Date expiration:</strong> {{ formatDate(data.details.passeport?.dateExpiration) }}</div>
          <div><strong>Pays délivrance:</strong> {{ data.details.passeport?.paysDelivrance || '-' }}</div>
        </div>
      </article>
    </div>

    <article class="panel accent-visa">
      <h3>Visa</h3>
      <div class="info-grid">
        <div><strong>Référence:</strong> {{ data.details.visa?.reference || '-' }}</div>
        <div><strong>Date début:</strong> {{ formatDate(data.details.visa?.dateDebut) }}</div>
        <div><strong>Date fin:</strong> {{ formatDate(data.details.visa?.dateFin) }}</div>
        <div><strong>Catégorie:</strong> {{ data.details.visa?.categorie || '-' }}</div>
      </div>
    </article>

    <article class="panel" id="liste-demandes">
      <h3>Liste des demandes</h3>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Date demande</th>
              <th>Type</th>
              <th>Statut</th>
              <th>Visa</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="d in data.demandes" :key="d.id">
              <td>{{ d.id }}</td>
              <td>{{ formatDate(d.dateDemande) }}</td>
              <td>{{ d.typeDemande || '-' }}</td>
              <td>{{ d.statut || '-' }}</td>
              <td>{{ d.visa?.reference || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </article>

    <article class="panel">
      <h3>Historique des actions</h3>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Statut</th>
              <th>Commentaire</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="h in data.historiques" :key="h.id">
              <td>{{ formatDateTime(h.dateChangement) }}</td>
              <td>{{ h.statut || '-' }}</td>
              <td>{{ h.commentaire || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </article>
  </section>
</template>

<style scoped>
.result-card {
  margin-top: 18px;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.eyebrow {
  margin: 0 0 4px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #8b6340;
  font-size: 0.75rem;
  font-weight: 700;
}

.topbar h2 {
  margin: 0;
  color: #59381f;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.link-btn {
  border: 1px solid #8b6340;
  background: #6f4a2e;
  color: #fff2df;
  border-radius: 10px;
  padding: 9px 12px;
  font-weight: 600;
}

.link-btn.light {
  background: #e7cfab;
  color: #4b2f1a;
}

.chip {
  background: #e5c59b;
  border: 1px solid #be9464;
  color: #4b2f1a;
  border-radius: 999px;
  font-size: 0.85rem;
  padding: 5px 10px;
}

.panel.highlight {
  background: linear-gradient(180deg, #fff5e8 0%, #f7e4c6 100%);
}

.accent-box {
  border-left: 5px solid #b67a42;
}

.accent-box.strong {
  border-left-color: #8b5730;
}

.accent-visa {
  border-left: 5px solid #6f4a2e;
  background: linear-gradient(180deg, #fff4e3 0%, #f8e6c9 100%);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.panel-head h3 {
  margin: 0;
  font-size: 1.45rem;
  font-weight: 900;
  color: #4f3118;
}

.panel {
  background: #fff5e7;
  border: 1px solid #ccb08a;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.panel h3 {
  margin: 0 0 10px;
  color: #5f3d21;
  font-size: 1.4rem;
  font-weight: 900;
  line-height: 1.1;
}

.panels-row {
  align-items: stretch;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 14px;
}

.info-grid div {
  color: #4b2f1a;
}

.span-2 {
  grid-column: span 2;
}

.table-wrap {
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 680px;
}

th,
td {
  border-bottom: 1px solid #e5cfae;
  padding: 8px;
  text-align: left;
}

th {
  color: #643f21;
  font-size: 0.83rem;
  text-transform: uppercase;
}

@media (max-width: 900px) {
  .topbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .grid.two,
  .info-grid,
  .meta-grid {
    grid-template-columns: 1fr;
  }

  .span-2 {
    grid-column: auto;
  }
}
</style>
