/* SIGEPPI - comportamiento común de todas las páginas */
(function () {
    // fecha de hoy en la barra superior
    var f = document.getElementById('fechaHoy');
    if (f) {
        var t = new Date().toLocaleDateString('es-PE', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
        f.textContent = t.charAt(0).toUpperCase() + t.slice(1);
    }

    // menú lateral en pantallas pequeñas
    var btn = document.getElementById('btnMenu');
    if (btn) {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            document.body.classList.toggle('menu-abierto');
        });
        document.addEventListener('click', function (e) {
            var menu = document.getElementById('menuLateral');
            if (document.body.classList.contains('menu-abierto') && menu && !menu.contains(e.target)) {
                document.body.classList.remove('menu-abierto');
            }
        });
    }

    // RNF-07: confirmación estándar antes de acciones importantes (data-confirmar="mensaje")
    document.querySelectorAll('[data-confirmar]').forEach(function (el) {
        var evento = el.tagName === 'FORM' ? 'submit' : 'click';
        el.addEventListener(evento, function (e) {
            if (!window.confirm(el.getAttribute('data-confirmar'))) {
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });
    });

    // buscador rápido en tablas: <input data-filtrar="idTabla">
    document.querySelectorAll('input[data-filtrar]').forEach(function (input) {
        var tabla = document.getElementById(input.getAttribute('data-filtrar'));
        if (!tabla) { return; }
        input.addEventListener('input', function () {
            var q = input.value.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
            tabla.querySelectorAll('tbody tr').forEach(function (tr) {
                if (tr.classList.contains('vacio')) { return; }
                var txt = tr.textContent.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
                tr.style.display = txt.indexOf(q) >= 0 ? '' : 'none';
            });
        });
    });

    // evitar doble envío de formularios (doble clic en "Guardar")
    document.querySelectorAll('form[method="post"]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (e.defaultPrevented) { return; }
            setTimeout(function () {
                form.querySelectorAll('button[type="submit"]').forEach(function (b) { b.disabled = true; });
            }, 0);
        });
    });

    // las alertas de éxito se ocultan solas
    document.querySelectorAll('.alerta.ok').forEach(function (a) {
        setTimeout(function () { a.style.transition = 'opacity .4s'; a.style.opacity = '0';
            setTimeout(function () { a.remove(); }, 450); }, 6000);
    });
    // edición en línea: <button data-editar="idFormulario" data-f-campo="valor"...> llena el formulario
    document.querySelectorAll('[data-editar]').forEach(function (b) {
        b.addEventListener('click', function () {
            var form = document.getElementById(b.getAttribute('data-editar'));
            if (!form) { return; }
            // los atributos HTML llegan en minúsculas: se buscan los campos sin distinguir mayúsculas
            var campos = {};
            Array.prototype.forEach.call(form.elements, function (el) { if (el.name) { campos[el.name.toLowerCase()] = el; } });
            Array.prototype.forEach.call(b.attributes, function (a) {
                if (a.name.indexOf('data-f-') !== 0) { return; }
                var campo = campos[a.name.substring(7).toLowerCase()];
                if (!campo) { return; }
                if (campo.type === 'checkbox') { campo.checked = a.value === 'true'; }
                else { campo.value = a.value; }
                campo.dispatchEvent(new Event('change'));
            });
            form.querySelectorAll('.solo-nuevo').forEach(function (el) { el.classList.add('oculto'); });
            var t = form.querySelector('[data-titulo]');
            if (t) { t.textContent = t.getAttribute('data-titulo'); }
            form.scrollIntoView({ behavior: 'smooth', block: 'start' });
            var primero = form.querySelector('input:not([type=hidden]), select');
            if (primero) { primero.focus(); }
        });
    });
})();
