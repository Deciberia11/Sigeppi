<%-- Página de inicio: manda al login (o al módulo del usuario si ya inició sesión) --%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
