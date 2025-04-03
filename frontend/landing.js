console.log("Script loaded: landing.js");

window.activateTituloOnce = () => {
    console.log("Entrou na função")
    const wrapper = document.getElementsByClassName('titulo')[0];
    if (!wrapper) {
        console.log("Error getting component.");
        return;
    }
    console.log("Got component.");
    let activated = false;

    wrapper.addEventListener('mouseenter', () => {
        console.log("Before activated");
        if (!activated) {
            wrapper.classList.add('slide');
            activated = true;
        }
    });
};

window.activateTituloOnce(); // só é chamada depois do DOM carregar
