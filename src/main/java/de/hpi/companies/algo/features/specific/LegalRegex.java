package de.hpi.companies.algo.features.specific;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class LegalRegex extends StringFeature {
	private static final String[] REGEX = {

			// Australia
			"[Nn][Oo]\\s+[Ll]iability|\\s+[Nn]\\.?\\s*[Ll]\\.?", // No liability, NL
			"[Pp]roprietary\\s+[Ll]imited\\s+[Cc]ompany|\\s+[Pp]\\.?\\s*[Tt]\\.?\\s*[Yy]\\.?\\s*[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?", // Proprietary Limited Company, Pty. Ltd.
			"[Uu]nlimited\\s+[Pp]roprietary|\\s+[Pp]\\.?\\s*[Tt]\\.?\\s*[Yy]\\.?", // Unlimited Proprietary, Pty. Ltd.

			// Chile
			"[Ee]mpresa\\s+[Ii]ndividual\\s+[Dd][Ee]\\s+[Rr]esponsabilidad\\s+[Ll]imitada|\\s+[Ee]\\.?\\s*[Ii]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?",  // Empresa Individual de Responsabilidad Limitada, EIRL
			"[Ss]ociedad\\s+[Dd][Ee]\\s+[Gg]arantia\\s+[Rr]eciproca|\\s+[Ss]\\.?\\s*[Gg]\\.?\\s*[Rr]\\.?",  // Sociedad de Garantia Reciproca, SGR

			// Czech Republic
			"[Aa]kciová\\s+[Ss]polečnost|\\s+[Aa]\\.?\\s*[Ss]\\.?|\\s+[Aa]\\.?\\s*[Kk]\\.?\\s*[Cc]\\.?\\s+[Ss]\\.?\\s*[Pp]\\.?\\s*[Oo]\\.?\\s*[Ll]\\.?",  // Akciová společnost, a.s., akc. spol.
			"[Ss]polečnost\\s+[Ss]\\s+[Rr]učením\\s+[Oo]mezeným|\\s+[Ss]\\.?\\s*[Pp]\\.?\\s*[Oo]\\.?\\s*[Ll]\\.?\\s*[Ss]\\.?\\s*[Rr]\\.?\\s*[Oo]\\.?",  // Společnost s ručením omezeným, s.r.o., spol. s r.o.
			"[Vv]eřejná\\s+[Oo]bchodní\\s+[Ss]polečnost|\\s+[Vv]\\.\\s*[Oo]\\.\\s*[Ss]\\.",  // veřejná obchodní společnost, v.o.s.
			"[Kk]omanditní\\s+[Ss]polečnost|\\s+[Kk]\\.\\s*[Ss]\\.",  // komanditní společnost, k.s.
			"[Oo]becně\\s+[Pp]rospěšná\\s+[Ss]polečnost|\\s+[Oo]\\.\\s*[Pp]\\.\\s*[Ss]\\.",  // obecně prospěšná společnost, o.p.s.
			"[Ss]\\.?\\s*[Rr]\\.?\\s*[Oo]\\.?",  // s.r.o.

			// Denmark
			"[Ii]værksætterselskab|\\s+[Ii]\\.?\\s*[Vv]\\.?\\s*[Ss]\\.?",  // Iværksætterselskab, IVS
			"[Aa]npartsselskab|\\s+[Aa]\\.?\\s*[Pp]\\.?\\s*[Ss]\\.?",  // Anpartsselskab, APS
			"[Ii]nteressentskab|\\s+[Ii]\\.?\\s*/\\.?\\s*[Ss]\\.?",  // Interessentskab, I/S
			"[Aa]ktieselskab|\\s+[Aa]\\.?\\s*/\\.?\\s*[Ss]\\.?",  // Aktieselskab, A/S
			"[Kk]ommanditselskab|\\s+[Kk]\\.?\\s*/\\.?\\s*[Ss]\\.?",  // Kommanditselskab, K/S
			"[Gg]ensidigt\\s+[Ss]elskab|\\s+[Gg]\\.?\\s*/\\.?\\s*[Ss]\\.?",  // Gensidigt selskab, G/S
			"[Aa]ndelsselskab\\s+[Mm]ed\\s+[Bb]egrænset\\s+[Aa]nsvar|\\s+[Aa]\\.?\\s*[Mm]\\.?\\s*[Bb]\\.?\\s*[Aa]\\.?",  // Andelsselskab med begrænset ansvar, AMBA
			"[Ff]orening\\s+[Mm]ed\\s+[Bb]egrænset\\s+[Aa]nsvar|\\s+[Ff]\\.?\\s*[Mm]\\.?\\s*[Bb]\\.?\\s*[Aa]\\.?",  // Forening med begrænset ansvar, FMBA
			"[Ss]elskab\\s+[Mm]ed\\s+[Bb]egrænset\\s+[Aa]nsvar|\\s+[Ss]\\.?\\s*[Mm]\\.?\\s*[Bb]\\.?\\s*[Aa]\\.?",  // Selskab med begrænset ansvar, FMBA
			"[Pp]artnerselskab|[Kk]ommanditaktieselskab|\\s+[Pp]\\.?\\s*/\\.?\\s*[Ss]\\.?",  // Partnerselskab, Kommanditaktieselskab, P/S
			"[Ee]nkeltmandsvirksomhed",  // Enkeltmandsvirksomhed
			"[Pp]artsrederi",  // Partsrederi
			"[Ff]orening",  // Forening

			// ---------- Kommanditgesellschaft

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Gesellschaft mit beschränkter Haftung &+ Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung &+ Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // GmbH &+ Co. Kommanditgesellschaft
			"[Gg]\\.?\\s*[Mm]\\.?\\s*[Bb]\\.?\\s*[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?\\s*[Gg]\\.?", // GmbH &+ Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Gesellschaft mit beschränkter Haftung u. Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung u. Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // GmbH u. Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // GmbH u. Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Gesellschaft mit beschränkter Haftung und Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung und Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // GmbH und Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // GmbH und Co. KG

			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Aktiengesellschaft &+ Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Aktiengesellschaft &+ Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // AG &+ Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // AG &+ Co. KG

			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Aktiengesellschaft u. Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Aktiengesellschaft u. Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // AG u. Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // AG u. Co. KG

			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Aktiengesellschaft und Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Aktiengesellschaft und Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // AG und Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // AG und Co. KG

			// ----------

			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Societas Europaea &+ Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Societas Europaea &+ Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // AG &+ Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // SE &+ Co. KG

			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Societas Europaea u. Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Societas Europaea u. Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // SE u. Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // SE u. Co. KG

			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Societas Europaea und Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Societas Europaea und Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // SE und Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // SE und Co. KG

			// ----------

			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Unternehmergesellschaft &+ Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Unternehmergesellschaft &+ Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // UG &+ Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // UG &+ Co. KG

			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Unternehmergesellschaft u. Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Unternehmergesellschaft u. Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // UG u. Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // UG u. Co. KG

			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft", // Unternehmergesellschaft und Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?", // Unternehmergesellschaft und Co. KG
			"[Uu]\\.?[Gg]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*Kommanditgesellschaft",  // UG und Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?",  // UG und Co. KG

			// ---------- Kommanditgesellschaft auf Aktien

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Gesellschaft mit beschränkter Haftung &+ Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Gesellschaft mit beschränkter Haftung &+ Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // GmbH &+ Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // GmbH &+ Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Gesellschaft mit beschränkter Haftung u. Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Gesellschaft mit beschränkter Haftung u. Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // GmbH u. Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // GmbH u. Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Gesellschaft mit beschränkter Haftung und Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Gesellschaft mit beschränkter Haftung und Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // GmbH und Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // GmbH und Co. KG

			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Aktiengesellschaft &+ Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Aktiengesellschaft &+ Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // AG &+ Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // AG &+ Co. KG

			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Aktiengesellschaft u. Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",// Aktiengesellschaft u. Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // AG u. Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // AG u. Co. KG

			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Aktiengesellschaft und Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Aktiengesellschaft und Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // AG und Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // AG und Co. KG

			// ----------

			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Societas Europaea &+ Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Societas Europaea &+ Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // AG &+ Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // SE &+ Co. KG

			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Societas Europaea u. Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Societas Europaea u. Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // SE u. Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // SE u. Co. KG

			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Societas Europaea und Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Societas Europaea und Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // SE und Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // SE und Co. KG

			// ----------

			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Unternehmergesellschaft &+ Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Unternehmergesellschaft &+ Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // UG &+ Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // UG &+ Co. KG

			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Unternehmergesellschaft u. Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Unternehmergesellschaft u. Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // UG u. Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // UG u. Co. KG

			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien", // Unternehmergesellschaft und Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Unternehmergesellschaft und Co. KG
			"[Uu]\\.?[Gg]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien",  // UG und Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?",  // UG und Co. KG

			// ---------- Offene Handelsgesellschaft, OHG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Gesellschaft mit beschränkter Haftung &+ Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung &+ Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // GmbH &+ Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // GmbH &+ Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Gesellschaft mit beschränkter Haftung u. Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung u. Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // GmbH u. Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // GmbH u. Co. KG

			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Gesellschaft mit beschränkter Haftung und Co. Kommanditgesellschaft
			"Gesellschaft\\s+mit\\s+beschränkter\\s+Haftung\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Gesellschaft mit beschränkter Haftung und Co. KG
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // GmbH und Co. Kommanditgesellschaft
			"[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // GmbH und Co. KG

			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Aktiengesellschaft &+ Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Aktiengesellschaft &+ Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // AG &+ Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // AG &+ Co. KG

			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Aktiengesellschaft u. Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Aktiengesellschaft u. Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // AG u. Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // AG u. Co. KG

			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Aktiengesellschaft und Co. Kommanditgesellschaft
			"Aktiengesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Aktiengesellschaft und Co. KG
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // AG und Co. Kommanditgesellschaft
			"[Aa]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // AG und Co. KG

			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Societas Europaea &+ Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Societas Europaea &+ Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // AG &+ Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // SE &+ Co. KG

			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Societas Europaea u. Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Societas Europaea u. Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // SE u. Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // SE u. Co. KG

			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Societas Europaea und Co. Kommanditgesellschaft
			"Societas\\s+Europaea\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Societas Europaea und Co. KG
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // SE und Co. Kommanditgesellschaft
			"[Ss]\\.?[Ee]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // SE und Co. KG

			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Unternehmergesellschaft &+ Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Unternehmergesellschaft &+ Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // UG &+ Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // UG &+ Co. KG

			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Unternehmergesellschaft u. Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Unternehmergesellschaft u. Co. KG
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // UG u. Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // UG u. Co. KG

			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft", // Unternehmergesellschaft und Co. Kommanditgesellschaft
			"Unternehmergesellschaft\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Unternehmergesellschaft und Co. KG
			"[Uu]\\.?[Gg]\\.?[Bb]\\.?[Hh]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]ffene\\s+[Hh]andelsgesellschaft",  // UG und Co. Kommanditgesellschaft
			"[Uu]\\.?[Gg]\\.?\\s+und\\s+[Cc][Oo]\\.?\\s*[Oo]\\.?[Hh]\\.?[Gg]\\.?",  // UG und Co. KG

			"Einzelunternehmen|eingetragener\\s+Kaufmann|\\s[Ee]\\.?\\s*[Kk]\\.?|\\s[Ee]\\.?\\s*[Kk]\\.?[Ff]\\.?[Mm]\\.?|\\s[Ee]\\.?\\s*[Kk]\\.?[Ff]\\.?[Rr]\\.?", // Einzelunternehmen, eingetragener Kaufmann, e.K., e.Kfm., e.Kfr.
			"[Gg]esellschaft\\s+[Bb]ürgerlichen\\s+[Rr]echts|[Gg]\\.?[Bb]\\.?[Rr]\\.?|[Gg]\\.?[Dd]\\.?[Bb]\\.?[Rr]\\.?", // Gesellschaft bürgerlichen Rechts, GbR
			"[Uu]nternehmergesellschaft\\s*\\(?[Hh]aftungsbeschränkt\\)?|\\s[Uu]\\.?[Gg]\\.?\\s*\\(?[Hh]aftungsbeschränkt\\)?", // Unternehmergesellschaft, UG
			"[Kk]ommanditgesellschaft\\s+[Aa]uf\\s+[Aa]ktien|[Kk]\\.?[Gg]\\.?[Aa]\\.?[aA]\\.?", // Kommanditgesellschaft auf Aktien, KGaA
			"[Kk]ommanditgesellschaft|\\s[Kk]\\.?[Gg]\\.?", // Kommanditgesellschaft, KG
			"[Gg]esellschaft\\s+mit+beschränkter\\s+[Hh]aftung|[Gg]\\.?[Mm]\\.?[Bb]\\.?[Hh]\\.?", // Gesellschaft mit beschränkter Haftung, GmbH
			"[Uu]nternehmergesellschaft\\s+\\(?haftungsbeschränkt\\)?", // Unternehmergesellschaft (haftungsbeschränkt)
			"[Ee]ingetragene\\s+[Gg]enossenschaft|\\s[Ee]\\.?[Gg]\\.?", // eingetragene Genossenschaft, e.G.
			"[pP]artnergesellschaft|\\s+[Pp]\\.?[Aa]\\.?[Rr]\\.?[Tt]\\.?[Gg]\\.?", // Partnergesellschaft, PartG
			"[Oo]ffene\\s+[Hh]andelsgesellschaft|\\s[Oo]\\.?[Hh]\\.?[Gg]\\.?", // Offene Handelsgesellschaft, OHG
			"[Uu]nternehmergesellschaft|\\s[Uu]\\.?[Gg]\\.?", // Unternehmergesellschaft, UG
			"[Aa]ktiengesellschaft|\\s[Aa]\\.?[Gg]\\.?", // Aktiengesellschaft, AG
			"[Ee]ingetragener\\s+[Vv]erein|\\s[Ee]\\.?[Vv]\\.?", // e.V., eingetrageber Verein
			"[Aa]ltrechtlicher\\s+[Vv]erein|\\s[Rr]\\.[Vv]\\.", // r.V., altrechtlicher Verein
			"gesellschaft\\s+[Mm]\\.?[Bb]\\.?[Hh]\\.?", // XYgesellschaft mbH

			"gesellschaft\b\\s+[Mm][Bb][Hh]\\s+[&+]\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?\\s*[Gg]\\.?", // XYgesellschaft mbH &+ Co KG
			"gesellschaft\b\\s+[Mm][Bb][Hh]\\s+u\\.?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?\\s*[Gg]\\.?", // XYgesellschaft mbH u. Co KG
			"gesellschaft\b\\s+[Mm][Bb][Hh]\\s+und?\\s+[Cc][Oo]\\.?\\s*[Kk]\\.?\\s*[Gg]\\.?", // XYgesellschaft mbH und Co KG


			// Singapore / Malaysia
			"[Pp]\\.?\\s*[Tt]\\.?\\s*[Ee]\\.?\\s*[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?",  // Pte. Ltd.
			"[Ss]\\.?\\s*[Dd]\\.?\\s*[Nn]\\.?\\s*[Bb]\\.?\\s*[Hh]\\.?\\s*[Dd]\\.?",  // Sdn. Bhd.
			"[Bb]\\.?\\s*[Hh]\\.?\\s*[Dd]\\.?",  // Bhd.

			// Guatemala
			"[Ss]ociedad\\s+[Dd][Ee]\\s+[Rr]esponsabilidad\\s+[Ll]imitada|\\s+C[íi]a\\.?\\s*Ltda\\.?", // Sociedad de Responsabilidad Limitada, Cía. Ltda.
			"[Ss]ociedad\\s+[Ee][Nn]\\s+[Cc]omandita\\s+[Pp][Oo][Rr]\\s+[Aa]cciones|\\s+Cía\\.?\\s*[Ss]\\.?\\s*[Cc]\\.?\\s*[Aa]\\.?", // Sociedad en Comandita por Acciones, Cía. SCA
			"[Ss]ociedad\\s+[Cc]olectiva|\\s+[Yy]\\s+Cía\\.?\\s*[Ss]\\.?\\s*[Cc]\\.?", // Sociedad Colectiva, y Cía. SC
			"[Ss]ociedad\\s+[Ee][Nn]\\s+[Cc]omandita\\s+[Ss]imple|\\s+[Yy]\\s+Cía\\.?\\s*[Ss]\\.?\\s*[Ee][Nn]\\s*[Cc]\\.?", // Sociedad en Comandita Simple, y Cía. S. en C.
			"[Ss]ociedad\\s+[Aa]nónima|\\s[Ss]\\.?\\s*[Aa]\\.?", // Sociedad Anónima, SA

			// Colombia
			"[Ss]ociedades\\s+[Pp][Oo][Rr]\\s+[Aa]cciones\\s+[Ss]implificada|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Ss]\\.?", // Sociedades por Acciones Simplificada, SAS
			"[Cc]omandita\\s+[Ss]imple|\\s+[Ss]\\.?\\s*[Ee][Nn]\\s*[Cc]\\.?",  // Comandita Simple, S. en C.
			"[Cc]omandita\\s+[Pp][Oo][Rr]\\s+[Aa]cciones|\\s+[Ss]\\.?\\s*[Cc]\\.?\\s*[Aa]\\.?", // Comandita por Acciones SCA
			"[Ss]ociedad\\s+[Cc]olectiva|\\s+[Ss]\\.\\s*[Cc]\\.",  // Sociedad Colectiva, SC
			"[Ee]mpresa\\s+[Uu]nipersonal|\\s+[Ee]\\.\\s*[Uu]\\.",  // Empresa Unipersonal, EU
			"[Ss]ociedad\\s+[Dd][Ee]\\s+[Rr]esponsabilidad\\s+[Ll]imitada|\\s+[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?\\s*[Aa]\\.?", // Sociedad de Responsabilidad Limitada, Ltda.

			// Hungary
			"[Kk]orlátolt\\s+[Ff]elelősségű\\s+[Tt]ársaság|\\s+[Kk]\\.?\\s*[Ff]\\.?\\s*[Tt]\\.?", // korlátolt felelősségű társaság, KFT
			"[Kk]özkereseti\\s+[Tt]ársaság|\\s+[Kk]\\.?\\s*[Kk]\\.?\\s*[Tt]\\.?", // közkereseti társaság, KKT
			"[Kk]özhasznú\\s+[Tt]ársaság|\\s+[Kk]\\.?\\s*[Hh]\\.?\\s*[Tt]\\.?", // közhasznú társaság, KHT
			"[Kk]özhasznú\\s+[Tt]ársaság|\\s+[Kk]\\.?\\s*[Vv]\\.?", // közös vállalat, KV
			"[Bb]etéti\\s+[Tt]ársaság|\\s+[Bb]\\.?\\s*[Tt]\\.?", // betéti társaság, BT
			"[Ee]gyéni\\s+[Cc]ég|\\s+[Ee]\\.\\s*[Cc]\\.", // egyéni cég, EC
			"[Ee]gyéni\\s+[Vv]állalkozó|\\s+[Ee]\\.\\s*[Vv]\\.", // egyéni vállalkozó, EV
			"[Nn]yilvánosan\\s+[Mm][űûü]köd[őõö]\\s+[Rr]észvénytársaság|\\s+[Nn]\\.?\\s*[Yy]\\.?\\s*[Rr]\\.?\\s*[Tt]\\.?", // nyilvánosan működő részvénytársaság, NYRT
			"[Zz]ártkör[ûűü]en\\s+[Mm][űûü]köd[őõö]\\s+[Rr]észvénytársaság|\\s+[Zz]\\.?\\s*[Rr]\\.?\\s*[Tt]\\.?", // zártközűen működő részvénytársaság, ZRT
			"[Rr]észvénytársaság|\\s+[Rr]\\.?\\s*[Tt]\\.?", // részvénytársaság, RT

			// Iceland
			"[Ee]inkahlutafélag|\\s+[Ee]\\.?\\s*[Hh]\\.?\\s*[Ff]\\.?", // einkahlutafélag, EHF
			"[Oo]pinbert\\s+[Hh]lutafélag|\\s+[Oo]\\s*[Hh]\\s*[Ff]\\.", // opinbert hlutafélag, OHF
			"[Hh]lutafélag|\\s+[Hh]\\s*[Ff]\\.", // hlutafélag, HF
			"[Ss]ameignarfélag|\\s+[Ss]\\s*[Ff]\\.", // sameignarfélag, SF
			"[Ss]jálfseignarstofnun|\\s+[Ss]\\s*[Ee]\\s*[Ss]\\.", // sjálfseignarstofnun, SES
			"[Ee]instaklingsfyrirtæki", // einstaklingsfyrirtæki
			"[Ss]amlagsfélag", // samlagsfélag
			"[Ss]amvinnufélag", // samvinnufélag

			// Israel
			"[Bb]e\'eravon\\s+[Mm]ugbal|\\s+[Bb]\\.?\\s*[Mm]\\.?", // Be'eravon Mugbal, BM

			// Italy
			"[Ss]ocietà\\s+[Ss]emplice|\\s+[Ss]\\.?\\s*[Ss]\\.?", // Società semplice, SS
			"[Ss]ocietà\\s+[Ii][Nn]\\s+[Nn][Oo][Mm][Ee]\\s+[Cc]ollettivo|\\s+[Ss]\\.?\\s*[Nn]\\.?\\s*[Cc]\\.?", // Società in nome collettivo, SNC
			"[Ss]ocietà\\s+[Ii][Nn]\\s+[Aa]ccomandita\\s+[Ss]emplice|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Ss]\\.?", // Società in accomandita semplice, SAS
			"[Ss]ocietà\\s+[Pp][Ee][Rr]\\s+[Aa]zioni|\\s+[Ss]\\.?\\s*[Pp]\\.?\\s*[Aa]\\.?", // Società per azioni, SPA
			"[Ss]ocietà\\s+[Ii][Nn]\\s+[Aa]ccomandita\\s+[Pp][Ee][Rr]\\s+[Aa]zioni|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Pp]\\.?\\s*[Aa]\\.?", // Società in accomandita per azioni, SAPA
			"[Ss]ocietà\\s+[Aa]\\s+[Rr]esponsabilità\\s+[Ll]imitata|\\s+[Ss]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?", // Società a responsabilità limitata, SRL
			"[Ss]ocietà\\s+[Cc]ooperativa\\s+[Aa]\\s+[Rr]esponsabilità\\s+[Ll]imitata|\\s+[Ss]\\.?\\s*[Cc]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?", // Società cooperativa a responsabilità limitata, SCRL

			// Kazakhstan
			"[Aa]ktsionernoe\\s+[Oo]bschestvo|\\s+[Aa]\\.?\\s*[Oo]\\.?", // Aktsionernoe obschestvo, AO
			"[Tt]ovarishchestvo\\s+[Ss]\\s+[Oo]granichennoy\\s+[Oo]tvetstvennostyu|\\s+[Tt]\\.\\s*[Oo]\\.\\s*[Oo]\\.", // Tovarishchestvo s ogranichennoy otvetstvennostyu, TOO
			"[Tt]ovarishchestvo\\s+[Ss]\\s+[Dd]opolnitelnoyu\\s+[Oo]tvetstvennostyu|\\s+[Tt]\\.\\s*[Oo]\\.\\s*[Oo]\\.", // Tovarishchestvo s dopolnitelnoyu otvetstvennostyu, TDO
			"[Gg]osudarstvenoe\\s+[Pp]redpriyatie|\\s+[Gg]\\.?\\s*[Pp]\\.?", // Gosudarstvenoe predpriyatie, GP
			"[Kk]omanditnoe\\s+[Tt]ovarishchestvo|\\s+[Kk]\\.\\s*[Tt]\\.", // Komanditnoe Tovarishchestvo, KT
			"[Pp]olnoe\\s+[Tt]ovarishchestvo|\\s+[Pp]\\.\\s*[Tt]\\.", // Polnoe Tovarishchestvo, PT
			"[Pp]otrebibitelskii\\s+[Kk]ooperativ|\\s+[Pp]\\.?\\s*[Tt]\\.?\\s*[Kk]\\.?", // Potrebibitelskii Kooperativ, PtK
			"[Rr]elitioznoe\\s+[Oo]bedinenie|\\s+[Pp]\\.\\s*[Oo]\\.", // Relitioznoe Obedinenie, PO
			"[Rr]elitioznoe\\s+[Oo]bedinenie|\\s+[Pp]\\.\\s*[Oo]\\.", // Relitioznoe Obedinenie, PO

			// Latvia
			"[Ss]abiedrība\\s+[Aa][Rr]\\s+[Ii]erobežotu\\s+[Aa]tbildību|\\s+[Ss]\\.?\\s*[Ii]\\.?\\s*[Aa]\\.?", // Sabiedrība ar ierobežotu atbildību, SIA
			"[Aa]kciju\\s+[Ss]abiedrība|\\s+[Aa]\\.?\\s*[Ss]\\.?", // Akciju sabiedrība, AS
			"[Ii]ndividuālais\\s+[Kk]omersants|\\s+[Ii]\\.\\s*[Kk]\\.", // Individuālais komersants, IK
			"[Pp]ilnsabiedrība", // Komandītsabiedrība, PS
			"[Bb]ezpeļņas\\s+[Oo]rganizācija|\\s+[Bb]\\.\\s*[Oo]\\.", // Bezpeļņas organizācija, BO
			"Ārzemju\\s[Kk]omersanta\\s+[Ff]iliāle|\\s+Ā\\.?\\s*[Kk]\\.?\\s*[Ff]\\.?", // Ārzemju komersanta filiāle, ĀKF
			"[Kk]omandītsabiedrība|\\s+[Kk]\\.\\s*[Ss]\\.", // Komandītsabiedrība, KS

			// Lebanon
			"[Ss]ociete\\s+[Aa]nonyme\\s+[Ll]ibanaise|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Ll]\\.?", // Societe Anonyme Libanaise, SAL
			"[Ss]ociete\\s+[Aa]nonyme\\s+[Ll]ibanaise|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Ll]\\.?", // Societe Anonyme Libanaise, SAL

			// Lithuania
			"[Uu]ždaroji\\s+[Aa]kcinė\\s+[Bb]endrovė|\\s+[Uu]\\.?\\s*[Aa]\\.?\\s*[Bb]\\.?",  // Uždaroji akcinė bendrovė, UAB
			"[Aa]kcinė\\s+[Bb]endrovė|\\s+[Aa]\\.?\\s*[Bb]\\.?",  // Akcinė bendrovė, AB
			"[Mm]ažoji\\s+[Bb]endrija|\\s+[Mm]\\.?\\s*[Bb]\\.?",  // Mažoji bendrija, MB

			// Luxembourg
			"[Ss]ociété\\s+[Aa]nonyme|\\s+[Ss]\\.?\\s*[Aa]\\.?",  // Société anonyme, SA
			"[Ss]ociété\\s+à\\s+[Rr]esponsabilité\\s+[Ll]imitée|\\s+[Ss]\\.?\\s*[Aaà]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?",  // Société à responsabilité limitée, SARL

			// Macedonia
			"[Aa]kcionersko\\s+[Dd]ruštvo|\\s+[Aa]\\.\\s*[Dd]\\.", // Akcionersko Društvo, AD
			"[Dd]ruštvo\\s+[Ss][Oo]\\s+[Oo]grKkičena\\s+[Oo]dgovornost\\s+[Oo][Dd]\\s+[Ee]dno\\s+[Ll]ice|\\s+[Dd]\\.?\\s*[Oo]\\.?\\s*[Oo]\\.?\\s*[Ee]\\.?\\s*[Ll]\\.?", // Društvo so Ograničena Odgovornost od Edno Lice, DOOEL
			"[Kk]omanditno\\s+[Dd]ruštvo|\\s+[Kk]\\.\\s*[Dd]\\.", // Komanditno Društvo, KD
			"[Kk]omanditno\\s+[Dd]ruštvo\\s+[Ss][Oo]\\s+[Aa]kcie|\\s+[Kk]\\.\\s*[Dd]\\.\\s*[Aa]\\.", // Komanditno Društvo so Akcie, KDA
			"[Jj]avno\\s+[Tt]rgovsko\\s+[Dd]ruštvo|\\s+[Jj]\\.\\s*[Tt]\\.\\s*[Dd]\\.", // Javno Trgovsko Društvo, JTD

			// Mexico
			"[Ss]ociedad\\s+[Dd][Ee]\\s+[Rr]esponsabilidad\\s+[Ll]imitada|\\s+[Ss]\\.?\\s*[Dd][Ee]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?", // Sociedad de Responsabilidad Limitada, S. de R.L.
			"[Ss]ociedad\\s+[Ee][Nn]\\s+[Cc]omandita\\s+[Ss]imple|\\s+[Ss]\\.?\\s*[Ee][Nn]\\.?\\s*[Cc]\\.?", // Sociedad en Comandita Simple, S. en C.
			"[Ss]ociedad\\s+[Ee][Nn]\\s+[Cc]omandita\\s+[Pp][Oo][Rr]\\s+[Aa]cciones|\\s+[Ss]\\.?\\s*[Ee][Nn]\\.?\\s*[Cc]\\.?\\s*[Pp][Oo][Rr]\\s*[Aa]\\.?", // Sociedad en Comandita por Acciones, S. en C. por A.
			"[Ss]ociedad\\s+[Aa]nónima\\s+[Bb]ursátil|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Bb]\\.?", // Sociedad Anónima Bursátil, SAB
			"[Ss]ociedad\\s+[Aa]nónima\\s+[Pp]romotora\\s+[Dd][Ee]\\s+[Ii]nversión|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Pp]\\.?\\s*[Ii]\\.?", // Sociedad Anónima Promotora de Inversión, SAPI

			// Norway
			"[Aa]llmennaksjeselskap|\\s+[Aa]\\.?\\s*[Ss]\\.?\\s*[Aa]\\.?", // Allmennaksjeselskap, ASA
			"[Aa]ksjeselskap|\\s+[Aa]\\.?\\s*[Ss]\\.?", // Aksjeselskap, AS
			"[Aa]nsvarlig\\s+[Ss]elskap|\\s+[Aa]\\.?\\s*[Nn]\\.?\\s*[Ss]\\.?", // Ansvarlig selskap, ANS
			"[Ss]elskap\\s+[Mm]ed\\s+[Bb]egrenset\\s+[Aa]nsvar|\\s+[B]\\.\\s*[A]\\.", // Selskap med begrenset ansvar, BA
			"[Bb]orettslag|\\s+[Bb]\\.?\\s*[Ll]\\.?", // Borettslag, BL
			"[Ss]elskap\\s+[Mm]ed\\s+[Dd]elt\\s+[Aa]nsvar|\\s+[Dd]\\.\\s*[Aa]\\.", // Selskap med delt ansvar, DA
			"[Ff]ylkeskommunalt\\s+[Ff]oretak|\\s+[Ff]\\.?\\s*[Kk]\\.?\\s*[Ff]\\.?", // Fylkeskommunalt foretak, FKF
			"[Ii]nterkommunalt\\s+[Ss]elskap|\\s+[Ii]\\.?\\s*[Kk]\\.?\\s*[Ss]\\.?", // Interkommunalt selskap, IKS
			"[Hh]elseforetak|\\s+[Hh]\\.?[Ff]\\.?", // helseforetak, HF
			"[Kk]ommunalt\\s+[Ff]oretak|\\s+[Kk]\\.?[Ff]\\.?", // Kommunalt foretak, KF
			"[Kk]ommandittselskap|\\s+[Kk]\\.?\\s*[Ss]\\.?", // Kommandittselskap, KS
			"[Nn]orskregistrert\\s+[Uu]tenlandsk\\s+[Ff]oretak|\\s+[Nn]\\.?\\s*[Uu]\\.?\\s*[Ff]\\.?", // Norskregistrert utenlandsk foretak, NUF
			"[Rr]egionalt\\s+[Hh]elseforetak|\\s+[Rr]\\.?\\s*[Hh]\\.?\\s*[Ff]\\.?", // regionalt helseforetak, RHF
			"[Ss]tatsforetak|\\s+[Ss]\\.?[Ff]\\.?", // Statsforetak, SF
			"[Kk]ommandittselskap|\\s+[Kk]\\.?\\s*[Ss]\\.?", // Kommandittselskap, KS
			"[Ee]nkeltpersonforetak", // Enkeltpersonforetak

			// Oman
			"[Ss]ociété\\s+[Aa]nonyme\\s+[Oo]manaise\\s+[Gg]énérale|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Oo]\\.?\\s*[Gg]\\.?", // Société Anonyme Omanaise Générale, SAOG
			"[Ss]ociété\\s+[Aa]nonyme\\s+[Oo]manaise\\s+[Cc]lose|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Oo]\\.?\\s*[Cc]\\.?", // Société Anonyme Omanaise Close, SAOC

			// Pakistan
			"[Ss]\\.?\\s*[Mm]\\.?\\s*[Ee]\\.?\\s*[Pp]\\.?\\s*[Vv]\\.?\\s*[Tt]\\.?", // SME Pvt
			"[Pp]rivate\\s+[Ll]imited\\s+[Cc]ompany|\\s+[Pp]rivate\\s+[Ll]imited|\\s+[Pp]\\.?\\s*[Vv]\\.?\\s*[Tt]\\.?\\s*[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?",  // Private Limited Company, Pvt. Ltd.

			// Peru
			"[Ss]ociedad\\s+[Aa]nónima\\s+[Aa]bierta|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Aa]\\.?", // Sociedad Anónima Abierta, SAA

			// Philippines
			"[Cc]ooperative|\\s+[Cc]\\.?\\s*[Oo]\\.?\\s*[Oo]\\.?\\s*[Pp]\\.?",  // Cooperative, Coop.

			// Poland
			"[Ss]półka\\s+[Kk]omandytowo\\s+\\-?\\s+[Aa]kcyjna|\\s+[Ss]\\.?\\s*[Kk]\\.?\\s*[Aa]\\.?", // spółka komandytowo - akcyjna, SKA
			"[Ss]półka\\s+[Jj]awna|\\s+[Ss][Pp]\\.?\\s*[Jj]\\.?", // spółka jawna, sp.j.
			"[Ss]półka\\s+[Kk]omandytowa|\\s+[Ss][Pp]\\.?\\s*[Kk]\\.?", // spółka komandytowa, sp.k.
			"[Ss]półka\\s+[Pp]artnerska|\\s+[Ss][Pp]\\.?\\s*[Pp]\\.?", // spółka partnerska, sp.p.
			"[Ss]półka\\s+[Zz]\\s+[Oo]graniczoną\\s+[Oo]dpowiedzialnością|\\s+[Ss][Pp]\\.?\\s*[Zz]\\.?\\s*[Oo]\\.?\\s*[Oo]\\.?", // spółka z ograniczoną odpowiedzialnością, sp. z.o.o.
			"[Ss]półdzielnia", // Spółdzielnia

			// Portugal
			"[Cc]ooperativa\\s+[Dd]e\\s+[Rr]esponsabilidade\\s+[Ll]imitada|\\s+[Cc]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?",  // Cooperativa de Responsabilidade Limitada, CRL
			"[Ll]imitada|\\s+[Ll]\\.?\\s*[Dd]\\.?\\s*[Aa]\\.?",  // Limitada, Lda
			"[Uu]nipessoal\\s+[Ll]\\.?\\s*[Dd]\\.?\\s*[Aa]\\.?",  // Unipessoal Lda

			// Romania
			"[Ss]ocietatea\\s+în\\s+[Nn]ume\\s+[Cc]olectiv|\\s+[Ss]\\.?\\s*[Nn]\\.?\\s*[Cc]\\.?",  // Societatea în nume colectiv, SNC
			"[Ss]ocietatea\\s+în\\s+[Cc]omandită\\s+[Ss]implă|\\s+[Ss]\\.?\\s*[Cc]\\.?\\s*[Ss]\\.?",  // Societatea în comandită simplă, SCS
			"[Ss]ocietatea\\s+în\\s+[Cc]omandită\\s+pe\\s+[Aa]cțiuni|\\s+[Ss]\\.?\\s*[Cc]\\.?\\s*[Aa]\\.?",  // Societatea în comandită pe acțiuni, SCA
			"[Ss]ocietatea\\s+[Cc]u\\s+[Rr]ăspundere\\s+[Ll]imitată|\\s+[Ss]\\.?\\s*[Rr]\\.?\\s*[Ll]\\.?",  // Societatea cu răspundere limitată, SRL
			"[Pp]ersoana\\s+[ff]izica\\s+[Aa]utorizata|\\s+[Pp]\\.?\\s*[Ff]\\.?\\s*[Aa]\\.?",  // persoana fizica autorizata, PFA

			// Russia
			"[Gg]osudarstvennoye\\s+[Uu]nitarnoye\\s+[Pp]redpriyatie|\\s[Gg]\\.?\\s*[Uu]?\\.?\\s*[Pp]\\.?", // Gosudarstvennoye unitarnoye predpriyatie / GUP / GP
			"[Ii]ndividualny\\s+[Pp]redprinimatel|\\s[Ii]\\.?\\s*[Pp]\\.?", // Individualny predprinimatel / IP
			"[Oo]bshchestvo\\s+s\\s+[Oo]granichennoy\\s+[Oo]tvetstvennostyu|\\s[Oo]\\.?\\s*[Oo]\\.?\\s*[Oo]\\.?", // Obshchestvo s ogranichennoy otvetstvennostyu / OOO
			"[Oo]tkrytoye\\s+s\\s+[Aa]ktsionernoye\\s+[Oo]bshchestvo|\\s[Oo]\\.?\\s*[Aa]\\.?\\s*[Oo]\\.?", // Otkrytoye aktsionernoye obshchestvo / OAO
			"[Zz]akrytoe\\s+s\\s+[Aa]ktsionernoye\\s+[Oo]bshchestvo|\\s[Zz]\\.?\\s*[Aa]\\.?\\s*[Oo]\\.?|\\s3\\s*[Aa]\\.?\\s*[Oo]\\.?", // Zakrytoe aktsionernoye obshchestvo / ZAO / 3AO

			// Serbia
			"[Aa]kcionarsko\\s+[Dd]ruštvo|\\s[Aa]\\.?\\s*[Dd]\\.?",  // akcionarsko društvo, AD
			"[Oo]rtačko\\s+[Dd]ruštvo|\\s[Oo]\\.?\\s*[Dd]\\.?",  // ortačko društvo, OD

			// Slovenia
			"[Dd]elniška\\s+[Dd]ružba|\\s[Dd]\\.?\\s*[Dd]\\.?", // Delniška družba, DD
			"[Kk]omanditna\\s+[Dd]ružba|\\s[Kk]\\.?\\s*[Dd]\\.?", // Komanditna družba, KD
			"[Ss]amostojni\\s+[Pp]odjetnik|\\s[Ss]\\.?\\s*[Pp]\\.?", // Samostojni podjetnik, SP
			"[Dd]ružba\\s+z\\s+[Oo]mejeno\\s+[Oo]dgovornostjo|\\s[Dd]\\.?\\s*[Oo]\\.\\s*[Oo]\\.?", // Družba z omejeno odgovornostjo, DOO
			"[Dd]ružba\\s+z\\s+[Nn]eomejeno\\s+[Oo]dgovornostjo|\\s[Dd]\\.?\\s*[Nn]\\.\\s*[Oo]\\.?", // Družba z neomejeno odgovornostjo, DNO

			// Slovakia
			"[Aa]kciová\\s+[Ss]poločnosť|\\s[Aa]\\.?\\s*[Ss]\\.?",  // Akciová spoločnosť, AS
			"[Kk]omanditná\\s+[Ss]poločnosť|\\s[Kk]\\.?\\s*[Ss]\\.?",  // Komanditná spoločnosť, KS
			"[Vv]erejná\\s+[Oo]bchodná\\s+[Ss]poločnosť|\\s[Vv]\\.\\s*[Oo]\\.\\s*[Ss]\\.",  // Verejná obchodná spoločnosť, VOS
			"[Ss]poločnosť\\s+s\\s+[Rr]učením\\s+[Oo]bmedzeným|\\s[Ss]\\.?\\s*[Pp]\\.?\\s*[Oo]\\.?\\s*[Ll]\\.?\\s*[Ss]\\.?\\s*[Rr]\\.?\\s*[Oo]\\.?",  // Spoločnosť s ručením obmedzeným, spol. s r.o.
			"[Ss]\\.?\\s*[Rr]\\.?\\s*[Oo]\\.?",  // SRO
			"[Dd]ružstvo",  // družstvo

			// Spain
			"[Ss]ociedad\\s+[Ll]imitada\\s+[Nn]ueva\\s+[Ee]mpresa|\\s+[Ss]\\.?\\s*[Ll]\\.?\\s*[Nn]\\.?\\s*[Ee]\\.?",  // Sociedad Limitada Nueva Empresa, SLNE
			"[Ss]ociedad\\s+[Aa]nónima\\s+[Dd][Ee]\\s+[Cc]apital\\s+[Vv]ariable|\\s+[Ss]\\.?\\s*[Aa]\\.?\\s*[Dd][Ee]\\s*[Cc]\\.?\\s*[Vv]\\.?",  // Sociedad Anónima de Capital Variable, S.A. de C.V.
			"[Ss]ociedad\\s+[Dd][Ee]\\s+[Rr]esponsabilidad\\s+[Ll]imitada\\s+[Dd][Ee]\\s+[Cc]apital\\s+[Vv]ariable|\\s+[Ss]\\.?\\s*[Dd][Ee]\\s*[Rr]\\.?\\s*[Ll]\\.?\\s*[Dd][Ee]\\s*[Cc]\\.?\\s*[Vv]\\.?",  // Sociedad de Responsabilidad Limitada de Capital Variable, S. de r.l. de C.V.

			"[Ss]ociedad\\s+[Ll]imitada\\s+[Ll]aboral|\\s+[Ss]\\.?\\s*[Ll]\\.?\\s*[Ll]\\.?",  // Sociedad Limitada Laboral, SLL
			"[Ss]ociedad\\s+[Aa]nónima\\s+[Dd]eportiva|\\s+S\\.?\\s*A\\.?\\s*D\\.?",  // Sociedad Aanónima Deportiva, SAD
			"[Ss]ociedad\\s+[Aa]nónima|\\s+[Ss]\\.?\\s*[Aa]\\.?",  // Sociedad Aanónima, SA
			"[Ss]ociedad\\s+[Cc]olectiva|\\s+S\\.?\\s*C\\.?",  // Sociedad Colectiva, SC
			"[Ss]ociedad\\s+[Cc]omanditaria|\\s+S\\.?\\s*C\\.?\\s*r\\.?\\s*a\\.?",  // Sociedad Comanditaria, S.Cra.
			"[Ss]ociedad\\s+[Cc]ooperativa|\\s+S\\.?\\s*C\\.?\\s*o\\.?\\s*o\\.?\\s*p\\.?",  // Sociedad Cooperativa, S.Coop.
			"[Ss]ociedad\\s+[Ll]imitada|\\s+[Ss]\\.?\\s*[Ll]\\.?",  // Sociedad Limitada, SL

			// Netherlands
			"[Vv]ennootschap\\s+[Oo]nder\\s+[Ff]irma|\\s+[Vv]\\.?\\s*[Oo]\\.?\\s*[Ff]\\.?",  // Vennootschap onder firma, VOF
			"[Cc]ommanditaire\\s+[Vv]ennootschap|\\s+[Cc]\\.?\\s*[Vv]\\.?",  // Commanditaire vennootschap, CV
			"[Bb]esloten\\s+[Vv]ennootschap|\\s+[Bb]\\.?\\s*[Vv]\\.?",  // Besloten vennootschap, BV
			"[Nn]aamloze\\s+[Vv]ennootschap|\\s+[Nn]\\.?\\s*[Vv]\\.?",  // Naamloze vennootschap, NV
			"[Bb]eperkte\\s+[Aa]ansprakelijkheid|\\s+[Bb]\\.\\s*[Aa]\\.",  // Beperkte Aansprakelijkheid, BA
			"[Mm]aatschap|\\s+[Mm]\\.?\\s*[Tt]\\.?\\s*[Ss]\\.?",  // Maatschap, MTS
			"[Ee]enmanszaak",  // Eenmanszaak

			// Sweden
			"[Aa]ktiebolag|\\s[Aa]\\.?\\s*[Bb]\\.?", // Aktiebolag, AB
			"[Hh]andelsbolag", // Handelsbolag
			"[Kk]ommanditbolag", // Kommanditbolag
			"[Ii]deell\\s+[Ff]örening", // Ideell förening

			// U.S.A
			// Partnerships
			"[Rr]egistered\\s+[Ll]imited\\s+[Ll]iability\\s+[Ll]imited\\s+[Pp]artnership|\\s+[Rr]\\.?\\s*[Ll]\\.?\\s*[Ll]\\.?\\s*[Ll]\\.?\\s*[Pp]\\.?", // Registered Limited Liability Limited Partnership, RLLLP
			"[Ll]imited\\s+[Ll]iability\\s+[Ll]imited\\s+[Pp]artnership|\\s+[Ll]\\.?\\s*[Ll]\\.?\\s*[Ll]\\.?\\s*[Pp]\\.?", // Limited Liability Limited Partnership, LLLP
			"[Ll]imited\\s+[Ll]iability\\s+[Pp]artnership|\\s+[Ll]\\.?\\s*[Ll]\\.?\\s*[Pp]\\.?", // Limited Liability Partnership, LLP
			"[Ll]imited\\s+[Pp]artnership|\\s+[Ll]\\.?\\s*[Pp]\\.?", // Limited Partnership, LP

			// Limited Liability Companies
			"[Pp]rofessional\\s+[Ll]imited\\s+[Ll]iability\\s+[Cc]ompany|\\s+[Pp]\\.?\\s*[Ll]\\.?\\s*[Ll]\\.?\\s*[Cc]\\.?", // Professional Limited Liability Company, PLLC
			"[Ll]imited\\s+[Ll]iability\\s+[Cc]ompany|[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?\\s*[Cc]\\.?\\s*[Oo]\\.?|\\s+[Ll]\\.?\\s*[Ll]\\.?\\s*[Cc]\\.?", // Limited Liability Company, Ltd. Co., LLC
			"[Ll]\\.?\\s*[Cc]\\.?", // LC

			// Corporations
			"[Ii]ncorporated|\\s+[Ii]\\.?\\s*[Nn]\\.?\\s*[Cc]\\.?", // Incorporated, Inc.
			"[Ll]imited|\\s+[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?",  // Limited, Ltd.
			"[Cc]orporation|\\s+[Cc]\\.?\\s*[Oo]\\.?\\s*[Rr]\\.?\\s*[Pp]\\.?", // Corporation, Corp.
			"[Cc]ompany|\\s+[Cc]\\.?\\s*[Oo]\\.?", // Company, Co.
			"[Pp]rofessional\\s+[Cc]orporation|\\s+[Pp]\\.?\\s*[Cc]\\.?",  // Professional corporation, PC.

			// United Kingdom (UK)
			"[Pp]ublic\\s+[Ll]imited\\s+[Cc]ompany|\\s+[Pp]\\.?\\s*[Ll]\\.?\\s*[Cc]\\.?", // Public Limited Company, PLC
			"[Cc]ommunity\\s+[Ii]nterest\\s+[Cc]ompany|\\s+[Cc]\\.?\\s*[Ii]\\.?\\s*[Cc]\\.?", // Community Interest Company, CIC
			"[Cc]haritable\\s+[Ii]ncorporated\\s+[Oo]rganisation|\\s+[Cc]\\.?\\s*[Ii]\\.?\\s*[Oo]\\.?", // Charitable Incorporated Organisation, CIO
			"[Uu]nlimited\\s+[Cc]ompany|\\s+[Uu]\\.?\\s*[Nn]\\.?\\s*[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?|\\s+[Uu]\\.?\\s*[Ll]\\.?\\s*[Tt]\\.?\\s*[Dd]\\.?"  // Unlimited company
	};
	
	private static final Pattern[] PATTERNS = Arrays.stream(REGEX).map(Pattern::compile).toArray(Pattern[]::new);

	@Override
	public void calculateFeatures(Token[] tokens) {
		boolean[] results = new boolean[tokens.length];
		List<Token> tokenList = Arrays.asList(tokens);
		for(int i=0;i<tokens.length;i++) {
			if(!results[i]) {
				for(int last=i+1;last<=tokens.length;last++) {
					String joined = join(tokenList.subList(i, last));
					for(Pattern p:PATTERNS) {
						if(p.matcher(joined).matches()) {
							for(int j=i;j<last;j++)
								results[j]=true;
						}
					}
				}
			}
		}
		
		for(int i=0;i<tokens.length;i++)
			tokens[i].setFeature(this, Boolean.toString(results[i]));
	}

	private String join(List<Token> l) {
		StringBuilder sb = new StringBuilder();
		sb.append(l.get(0).getRawForm());
		for(int i=1;i<l.size();i++) {
			if(l.get(i-1).hasWhitespaceAfter())
				sb.append(' ');
			sb.append(l.get(i).getRawForm());
		}
		return sb.toString();
	}
	
	
}
