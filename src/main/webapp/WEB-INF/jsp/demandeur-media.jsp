<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="framework.visa.entity.Demande" %>
<%@ page import="framework.visa.entity.Demandeur" %>
<%@ page import="framework.visa.entity.DemandeurPhotoSignature" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Photo et signature</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
    <style>
        body {
            background: radial-gradient(circle at top left, #f7f1e6, #fcfbf6 45%, #f4efe6 100%);
        }

        .media-page {
            max-width: 1280px;
            margin: 0 auto;
        }

        .media-page form {
            display: block;
        }

        .media-banner {
            background: linear-gradient(135deg, #fff8ee, #f6ecdc);
            border: 1px solid var(--border-light);
            border-radius: var(--radius-md);
            padding: 1.25rem 1.5rem;
            box-shadow: var(--shadow-sm);
            margin-bottom: 1.5rem;
        }

        .media-banner h1 {
            margin-bottom: 0.5rem;
        }

        .media-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 1.25rem;
            align-items: start;
        }

        .media-card {
            background: var(--bg-white);
            border-radius: var(--radius-md);
            border: 1px solid var(--border-light);
            box-shadow: var(--shadow-sm);
            padding: 1.25rem;
        }

        .media-card h2 {
            font-size: 1.1rem;
            margin-bottom: 0.9rem;
            padding-bottom: 0.6rem;
            border-bottom: 1px solid var(--border-light);
        }

        .media-stage {
            position: relative;
            border-radius: 14px;
            overflow: hidden;
            border: 1px solid var(--border-light);
            background: #1c1a17;
            height: 360px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .media-stage video,
        .media-stage img,
        .media-stage canvas {
            width: 100%;
            height: 100%;
            display: block;
        }

        #cameraVideo {
            object-fit: cover;
            background: #1c1a17;
        }

        .camera-placeholder {
            position: absolute;
            inset: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
            text-align: center;
            color: #f5eadc;
            background: linear-gradient(135deg, rgba(28, 26, 23, 0.92), rgba(70, 58, 41, 0.72));
            z-index: 2;
            pointer-events: none;
            font-weight: 600;
        }

        .media-preview {
            margin-top: 0.9rem;
            border: 1px solid var(--border-light);
            border-radius: 12px;
            padding: 0.75rem;
            background: #faf8f2;
        }

        .media-preview img {
            width: 100%;
            max-height: 320px;
            object-fit: contain;
            display: block;
            border-radius: 10px;
            background: white;
        }

        .signature-canvas {
            width: 100%;
            height: 260px;
            border-radius: 14px;
            border: 1px solid var(--border-light);
            background: #fff;
            touch-action: none;
            display: block;
            cursor: crosshair;
        }

        .media-actions {
            display: flex;
            gap: 0.75rem;
            flex-wrap: wrap;
            margin-top: 0.9rem;
        }

        .media-note {
            color: var(--text-secondary);
            font-size: 0.92rem;
            margin-top: 0.65rem;
        }

        .media-warning {
            margin-top: 0.75rem;
            color: #8a3a2d;
            font-weight: 600;
        }

        .media-summary {
            display: grid;
            gap: 0.7rem;
        }

        .media-summary-row {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            padding-bottom: 0.55rem;
            border-bottom: 1px dashed var(--border-light);
            color: var(--text-label);
            font-size: 0.94rem;
        }

        .media-summary-row strong {
            color: var(--text-primary);
        }

        .media-status {
            display: inline-flex;
            align-items: center;
            gap: 0.45rem;
            border-radius: 999px;
            padding: 0.35rem 0.75rem;
            background: #f4ede0;
            color: #6a4b31;
            font-size: 0.84rem;
            font-weight: 600;
        }

        .media-footer {
            margin-top: 1.2rem;
            display: flex;
            gap: 0.85rem;
            flex-wrap: wrap;
            align-items: center;
        }

        .media-error {
            background: #e47272;
            border-left: 4px solid #ff0000;
            padding: 0.9rem 1.25rem;
            border-radius: var(--radius-sm);
            color: #000000;
            font-size: 0.875rem;
            margin-bottom: 1.25rem;
        }

        .media-success {
            background: #e7f5e8;
            border-left: 4px solid #2f9e44;
            padding: 0.9rem 1.25rem;
            border-radius: var(--radius-sm);
            color: #1f5b2d;
            font-size: 0.875rem;
            margin-bottom: 1.25rem;
        }
    </style>
</head>
<body>
<%
    Demande demande = (Demande) request.getAttribute("demande");
    Demandeur demandeur = demande == null ? null : demande.getDemandeur();
    DemandeurPhotoSignature media = (DemandeurPhotoSignature) request.getAttribute("media");
    String source = (String) request.getAttribute("source");
    if (source == null || source.isBlank()) {
        source = "list";
    }
    String returnUrl = (String) request.getAttribute("returnUrl");
    if (returnUrl == null || returnUrl.isBlank()) {
        returnUrl = "/dossiers-en-cours";
    }

    String photoValue = media == null || media.getPhoto() == null ? "" : media.getPhoto();
    String signatureValue = media == null || media.getSignature() == null ? "" : media.getSignature();
    boolean hasPhoto = photoValue != null && !photoValue.isBlank();
    boolean hasSignature = signatureValue != null && !signatureValue.isBlank();
    boolean hasCompleteMedia = hasPhoto && hasSignature;

    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
%>

<div class="media-page">
    <div class="horizontal-sidebar">
        <a href="${pageContext.request.contextPath}/">Retour accueil</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours">Liste des demandes</a>
        <% if (demande != null && demande.getId() != null) { %>
            <a href="${pageContext.request.contextPath}/demande/<%= demande.getId() %>">Détails demande</a>
        <% } %>
    </div>

    <% if (message != null && !message.isEmpty()) { %>
    <p class="media-success"><%= message %></p>
    <% } %>

    <% if (error != null && !error.isEmpty()) { %>
    <p class="media-error"><%= error %></p>
    <% } %>

    <div class="media-banner">
        <h1>Photo et signature</h1>
        <p class="media-note">La photo et la signature ne sont pas obligatoires à la création, mais elles doivent être complètes avant le passage au statut scanne.</p>
        <div class="media-status">
            <span><%= hasCompleteMedia ? "Média complet" : "Média incomplet" %></span>
        </div>
    </div>

    <% if (demande == null || demandeur == null) { %>
        <div class="media-card">
            <p>Demande introuvable.</p>
        </div>
    <% } else { %>
    <form method="post" action="${pageContext.request.contextPath}/demandeur-media">
        <input type="hidden" name="demandeId" value="<%= demande.getId() %>">
        <input type="hidden" name="source" value="<%= source %>">
        <input type="hidden" id="photoData" name="photo" value="<%= photoValue %>">
        <input type="hidden" id="signatureData" name="signature" value="<%= signatureValue %>">

        <div class="media-grid">
            <div class="media-card">
                <h2>Résumé</h2>
                <div class="media-summary">
                    <div class="media-summary-row"><strong>Demande</strong><span>#<%= demande.getId() %></span></div>
                    <div class="media-summary-row"><strong>Demandeur</strong><span><%= demandeur.getNom() == null ? "-" : demandeur.getNom() %> <%= demandeur.getPrenom() == null ? "" : demandeur.getPrenom() %></span></div>
                    <div class="media-summary-row"><strong>Statut</strong><span><%= demande.getStatut() == null || demande.getStatut().getLibelle() == null ? "-" : demande.getStatut().getLibelle() %></span></div>
                </div>
                <p class="media-note">Cliquez sur <strong>Démarrer la caméra</strong>, puis <strong>Prendre photo</strong>. Pour la signature, utilisez le trackpad ou la souris dans la zone prévue.</p>
                <div class="media-footer">
                    <button type="button" class="secondary-action" id="startCameraBtn">Démarrer la caméra</button>
                    <button type="button" class="secondary-action" id="capturePhotoBtn">Prendre photo</button>
                    <button type="button" class="secondary-action" id="clearSignatureBtn">Effacer signature</button>
                </div>
                <p id="cameraStatus" class="media-warning" style="display:none;"></p>
            </div>

            <div class="media-card">
                <h2>Photo webcam</h2>
                <div class="media-stage">
                    <div id="cameraPlaceholder" class="camera-placeholder">Caméra en attente. Cliquez sur “Démarrer la caméra” si l’aperçu ne s’active pas automatiquement.</div>
                    <video id="cameraVideo" autoplay playsinline muted></video>
                </div>
                <div class="media-preview">
                    <img id="photoPreview" alt="Aperçu photo du demandeur" src="<%= hasPhoto ? photoValue : "" %>" <%= hasPhoto ? "" : "style=\"display:none;\"" %>>
                </div>
                <p class="media-note">La capture est enregistrée au format image pour être affichée ensuite dans les détails de la demande.</p>
            </div>

            <div class="media-card">
                <h2>Signature trackpad</h2>
                <canvas id="signatureCanvas" class="signature-canvas" width="900" height="260"></canvas>
                <div class="media-preview">
                    <img id="signaturePreview" alt="Aperçu signature du demandeur" src="<%= hasSignature ? signatureValue : "" %>" <%= hasSignature ? "" : "style=\"display:none;\"" %>>
                </div>
                <p class="media-note">Signez directement dans le rectangle blanc. Si une signature existe déjà, vous pouvez la remplacer.</p>
            </div>
        </div>

        <div class="media-footer">
            <button type="submit">Enregistrer la photo et la signature</button>
            <a class="secondary-action" href="<%= returnUrl %>">Retour</a>
        </div>
    </form>
    <% } %>
</div>

<script>
    const photoDataInput = document.getElementById('photoData');
    const signatureDataInput = document.getElementById('signatureData');
    const photoPreview = document.getElementById('photoPreview');
    const signaturePreview = document.getElementById('signaturePreview');
    const cameraVideo = document.getElementById('cameraVideo');
    const startCameraBtn = document.getElementById('startCameraBtn');
    const capturePhotoBtn = document.getElementById('capturePhotoBtn');
    const clearSignatureBtn = document.getElementById('clearSignatureBtn');
    const signatureCanvas = document.getElementById('signatureCanvas');
    const cameraStatus = document.getElementById('cameraStatus');
    const cameraPlaceholder = document.getElementById('cameraPlaceholder');

    let cameraStream = null;
    let cameraReady = false;
    let signatureDrawing = false;
    let signatureHasInk = false;

    function showCameraStatus(message) {
        if (!cameraStatus) {
            return;
        }
        cameraStatus.textContent = message;
        cameraStatus.style.display = message ? 'block' : 'none';
    }

    function setCameraPlaceholder(message, visible) {
        if (!cameraPlaceholder) {
            return;
        }

        cameraPlaceholder.textContent = message;
        cameraPlaceholder.style.display = visible ? 'flex' : 'none';
    }

    function syncSaveState() {
        const submitButton = document.querySelector('button[type="submit"]');
        if (!submitButton) {
            return;
        }

        const photoReady = photoDataInput && photoDataInput.value.trim() !== '';
        const signatureReady = signatureDataInput && signatureDataInput.value.trim() !== '';
        submitButton.disabled = !(photoReady && signatureReady);
    }

    async function startCamera() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            showCameraStatus('La caméra n\'est pas disponible dans ce navigateur.');
            setCameraPlaceholder('La caméra n\'est pas disponible dans ce navigateur.', true);
            return;
        }

        try {
            cameraReady = false;
            if (cameraStream) {
                cameraStream.getTracks().forEach((track) => track.stop());
            }
            setCameraPlaceholder('Ouverture de la caméra...', true);
            cameraStream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user' }, audio: false });
            cameraVideo.srcObject = cameraStream;
            await cameraVideo.play();
            cameraVideo.addEventListener('loadeddata', () => {
                cameraReady = true;
                setCameraPlaceholder('Caméra active.', false);
            }, { once: true });
            if (cameraVideo.readyState >= HTMLMediaElement.HAVE_CURRENT_DATA) {
                cameraReady = true;
                setCameraPlaceholder('Caméra active.', false);
            }
            showCameraStatus('Caméra active.');
        } catch (error) {
            showCameraStatus('Impossible d\'accéder à la caméra: ' + error.message);
            setCameraPlaceholder('Impossible d\'accéder à la caméra. Vérifiez la permission navigateur.', true);
        }
    }

    function capturePhoto() {
        if (!cameraReady || !cameraVideo || cameraVideo.videoWidth === 0 || cameraVideo.videoHeight === 0) {
            showCameraStatus('Démarrez d\'abord la caméra avant la capture.');
            return;
        }

        const snapshotCanvas = document.createElement('canvas');
        snapshotCanvas.width = cameraVideo.videoWidth;
        snapshotCanvas.height = cameraVideo.videoHeight;
        const context = snapshotCanvas.getContext('2d');
        context.drawImage(cameraVideo, 0, 0, snapshotCanvas.width, snapshotCanvas.height);

        const dataUrl = snapshotCanvas.toDataURL('image/jpeg', 0.92);
        if (photoDataInput) {
            photoDataInput.value = dataUrl;
        }
        if (photoPreview) {
            photoPreview.src = dataUrl;
            photoPreview.style.display = 'block';
        }
        syncSaveState();
    }

    function resizeSignatureCanvas() {
        const ratio = Math.max(window.devicePixelRatio || 1, 1);
        const rect = signatureCanvas.getBoundingClientRect();
        const existingData = signatureDataInput && signatureDataInput.value.trim() !== '' ? signatureDataInput.value : null;

        signatureCanvas.width = rect.width * ratio;
        signatureCanvas.height = rect.height * ratio;
        const context = signatureCanvas.getContext('2d');
        context.scale(ratio, ratio);
        context.lineWidth = 2.5;
        context.lineCap = 'round';
        context.lineJoin = 'round';
        context.strokeStyle = '#2c2b28';
        context.fillStyle = '#ffffff';
        context.fillRect(0, 0, rect.width, rect.height);

        if (existingData) {
            const image = new Image();
            image.onload = () => {
                context.drawImage(image, 0, 0, rect.width, rect.height);
            };
            image.src = existingData;
            signatureHasInk = true;
        }
    }

    function drawSignaturePoint(event) {
        const context = signatureCanvas.getContext('2d');
        const rect = signatureCanvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        if (!signatureDrawing) {
            context.beginPath();
            context.moveTo(x, y);
            signatureDrawing = true;
            return;
        }

        context.lineTo(x, y);
        context.stroke();
        signatureHasInk = true;
        if (signatureDataInput) {
            signatureDataInput.value = signatureCanvas.toDataURL('image/png');
        }
        if (signaturePreview) {
            signaturePreview.src = signatureDataInput.value;
        }
        syncSaveState();
    }

    function finishSignatureStroke() {
        signatureDrawing = false;
    }

    if (startCameraBtn) {
        startCameraBtn.addEventListener('click', startCamera);
    }

    if (capturePhotoBtn) {
        capturePhotoBtn.addEventListener('click', capturePhoto);
    }

    if (clearSignatureBtn) {
        clearSignatureBtn.addEventListener('click', () => {
            resizeSignatureCanvas();
            signatureHasInk = false;
            if (signatureDataInput) {
                signatureDataInput.value = '';
            }
            if (signaturePreview) {
                signaturePreview.removeAttribute('src');
                signaturePreview.style.display = 'none';
            }
            syncSaveState();
        });
    }

    if (signatureCanvas) {
        signatureCanvas.addEventListener('pointerdown', (event) => {
            signatureCanvas.setPointerCapture(event.pointerId);
            drawSignaturePoint(event);
        });
        signatureCanvas.addEventListener('pointermove', (event) => {
            if (!signatureDrawing) {
                return;
            }
            drawSignaturePoint(event);
        });
        signatureCanvas.addEventListener('pointerup', finishSignatureStroke);
        signatureCanvas.addEventListener('pointerleave', finishSignatureStroke);
    }

    if (signatureDataInput && signatureDataInput.value.trim() !== '' && signaturePreview) {
        signaturePreview.src = signatureDataInput.value;
        signaturePreview.style.display = 'block';
    }

    if (photoDataInput && photoDataInput.value.trim() !== '' && photoPreview) {
        photoPreview.src = photoDataInput.value;
        photoPreview.style.display = 'block';
    }

    window.addEventListener('resize', () => {
        if (signatureCanvas) {
            resizeSignatureCanvas();
        }
    });

    if (signatureCanvas) {
        resizeSignatureCanvas();
    }

    syncSaveState();

    startCamera();
</script>
</body>
</html>