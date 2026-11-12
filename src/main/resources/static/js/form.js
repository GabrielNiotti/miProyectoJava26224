const form = document.getElementById("form-contacto");
            form.addEventListener("submit", () => {
              // Esperar un poco para que Formspree tome los datos, si no capaz se borran antes de salir
              setTimeout(() => {
                form.reset();
              }, 500);
            });