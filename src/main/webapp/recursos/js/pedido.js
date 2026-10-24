/* CUS02 - arma el detalle del pedido en pantalla y calcula los totales (RN003).
   El servidor vuelve a calcular todo con los precios de la BD. */
(function () {
    var IGV = 0.18;
    var cuerpo = document.getElementById('cuerpoDetalle');
    var selProducto = document.getElementById('selProducto');
    var txtCantidad = document.getElementById('txtCantidad');
    var selCliente = document.getElementById('idCliente');

    function dinero(n) { return n.toFixed(2); }

    function recalcular() {
        var subtotal = 0;
        cuerpo.querySelectorAll('tr.linea').forEach(function (tr) {
            subtotal += parseFloat(tr.dataset.subtotal);
        });
        var igv = Math.round(subtotal * IGV * 100) / 100;
        document.getElementById('lblSubtotal').textContent = dinero(subtotal);
        document.getElementById('lblIgv').textContent = dinero(igv);
        document.getElementById('lblTotal').textContent = dinero(subtotal + igv);
        var vacio = cuerpo.querySelector('tr.vacio');
        if (vacio) { vacio.style.display = cuerpo.querySelector('tr.linea') ? 'none' : ''; }
    }

    document.getElementById('btnAgregar').addEventListener('click', function () {
        var cantidad = parseInt(txtCantidad.value, 10);
        if (isNaN(cantidad) || cantidad <= 0) {                       // RN005
            alert('La cantidad debe ser un número mayor a 0 (RN005).');
            return;
        }
        var op = selProducto.options[selProducto.selectedIndex];
        if (!op) { return; }
        var id = op.value, precio = parseFloat(op.dataset.precio);
        var nombre = op.dataset.nombre || op.textContent.split(' - S/')[0].trim();

        // si el producto ya está, se suma la cantidad
        var existente = cuerpo.querySelector('tr.linea[data-id="' + id + '"]');
        if (existente) {
            cantidad += parseInt(existente.dataset.cantidad, 10);
            existente.remove();
        }
        var sub = cantidad * precio;
        var tr = document.createElement('tr');
        tr.className = 'linea';
        tr.dataset.id = id; tr.dataset.cantidad = cantidad; tr.dataset.subtotal = sub;
        tr.innerHTML =
            '<td></td><td class="num">' + cantidad + '</td><td class="num">S/ ' + dinero(precio) + '</td>' +
            '<td class="num">S/ ' + dinero(sub) + '</td>' +
            '<td><button type="button" class="btn chico quitar">Quitar</button>' +
            '<input type="hidden" name="idProducto" value="' + id + '">' +
            '<input type="hidden" name="cantidad" value="' + cantidad + '"></td>';
        tr.firstChild.textContent = nombre;
        tr.querySelector('.quitar').addEventListener('click', function () { tr.remove(); recalcular(); });
        cuerpo.appendChild(tr);
        txtCantidad.value = 1;
        recalcular();
    });

    // mostrar los campos de cliente nuevo
    function toggleCliente() {
        var nuevo = selCliente.value === '';
        document.querySelectorAll('.nuevo-cliente').forEach(function (d) { d.classList.toggle('oculto', !nuevo); });
    }
    selCliente.addEventListener('change', toggleCliente);
    toggleCliente();

    document.getElementById('formPedido').addEventListener('submit', function (e) {
        if (!cuerpo.querySelector('tr.linea')) {                      // RN002
            e.preventDefault();
            alert('Agregue al menos un producto al pedido (RN002).');
            return;
        }
        if (selCliente.value === '' && !document.getElementById('nombreCliente').value.trim()) {
            e.preventDefault();
            alert('Ingrese el nombre del cliente nuevo (RN001).');
        }
    });
})();
