package io.github.formatttech.qualite.formatage;

/** Formule de politesse selon l'heure : le même code, mal formaté. */
public class Salutation
{
  public String pour( int heure ){
	if (heure<18) { return "Bonjour"; }
        return "Bonsoir";
  }
}
