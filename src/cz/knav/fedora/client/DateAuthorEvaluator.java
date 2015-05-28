package cz.knav.fedora.client;

import java.util.Calendar;
import java.util.regex.Pattern;

public class DateAuthorEvaluator {

    private static Pattern patternAuthor = null;
    private static Pattern patternIssued = null;
    
    private String dateLower;
    private int years;
    
    private int yearCount = 0;
    
    private int ixYearLast;
    
    protected String yearLast = null; //to be accessible in DateIssuedEvaluator
    private String yearBeforeLast = null;

    /*
    dle standardu - dateAuthor: RRRR-RRRR
    
    dle standardu - dateIssued:
    
    1. Pro nejvyssi urovne - titul periodika a titul monografie
    
    <dateIssued>
    datum  vydání  předlohy,  nutno  zaznamenat  v případě  titulu
     roky  v nichž  časopis  vycházel  (např. 1900‐1939),  přebírat  ve
    formě,  jak  je  zapsáno  v hodnotě  pole  v  katalogu
    odpovídá  hodnotě  z katalogizačního  záznamu,  pole  260,  podpole  „c“
    
    2. Pro rocnik periodika
    
    <dateIssued>
    datum  vydání  předlohy,  v případě  ročníku rok,  případně  rozsah
    let,  kdy  vyšel
    ‐ RRRR  – pokud  víme  rok
    ‐ RRRR‐RRRR  – rozsah  let
    - atribut "qualifier" - možnost  dalšího  upřesnění,  hodnota
    „approximate“ pro data, kde nevíme přesný údaj
    
    3. Pro číslo periodika a přílohu
    
    <dateIssued>
    datum  vydání  předlohy,  v případě  čísla  datum  dne,  kdy  vyšlo;
    musí  vyjádřit  den,  měsíc  a  rok,  dle  toho  jaké  údaje  jsou  k
    dispozici;
    nutno  zapsat  v následujících  podobách:
    ‐ DD.MM.RRRR – pokud  víme  den,  měsíc  i rok  vydání
    ‐ MM.RRRR  – pokud  víme  jen  měsíc  a  rok vydání
    ‐ RRRR – pokud  víme  pouze  rok
    ‐ DD.‐DD.MM.RRRR – vydání  pro  více  dní
    - MM.‐MM.RRRR – vydání  pro  více  měsíců
    - atribut - qualifier  – možnost  dalšího  upřesnění,  hodnota
    „approximate“  pro  data,  kde  nevíme  přesný  údaj
          
    ----------------------
    jiný typ "pomlček":
    DD.MM.RRRR – pokud víme den, měsíc i rok vydání
    RRRR – pokud víme pouze rok
    MM.RRRR – pokud víme jen měsíc a rok vydání
    DD.-DD.MM.RRRR – vydání pro více dní
    MM.-MM.RRRR – vydání pro více měsíců
    ----------------------
    */
    public static boolean isDateAlmostStandard(String date, boolean isDateAuthor) {
        boolean r = false;
        if (date != null) {
            String minusesAndSimilar = "[−‒–—―‐-]{0,5} {0,2}";
            if (isDateAuthor) {
                if (patternAuthor == null) {
                    patternAuthor = Pattern.compile(
                            "^\\d\\d\\d\\d {0,2}" + minusesAndSimilar
                            + "\\d\\d\\d\\d$");
                }
                r = patternAuthor.matcher(date.trim()).find();
            } else {
                String d0m0yyyy = 
                        "((\\d?\\d\\. {0,2})?"+
                        "\\d?\\d\\. {0,2})?"+
                        "\\d\\d\\d\\d {0,2}";
                if (patternIssued == null) {
                    patternIssued = Pattern.compile(
                            "^(" + d0m0yyyy +
                                "(" + minusesAndSimilar + d0m0yyyy + 
                            ")?)$|^"+
                            "(" + 
                                "(\\d?\\d\\. {0,2})?"+
                                "\\d?\\d\\. {0,2}"+
                                minusesAndSimilar + d0m0yyyy + 
                            ")$");
                }
                r = patternIssued.matcher(date.trim()).find();
                /*
                String[] aS = new String[] {
                        "2000",
                        "2000-2010",
                        "21.01.2000",
                        "01.2000",
                        "21. - 29.01.2000",
                        "01.-02.2000",
                        "21.01.–29.01.2000",
                        "21.1.2000  —29.01.2000",
                        "21.1.2000 -—--- 2000",
                        "01.01.99",    //ne
                        "01.01.99 - 20.01.99",//ne
                };
                for (int i = 0; i < aS.length; i++) {
                    Matcher m = patternIssued.matcher(aS[i]);
                    if (m.find()) {
                        System.out.println("ano " + aS[i]);
                    } else {
                        System.out.println("ne " + aS[i]);
                    }
                }
                */
            }
        }
        return r;
    }

    public static boolean isDateAuthorOk(String date, int years) {
        return (new DateAuthorEvaluator(date, years)).isDateOk();
    }

    public static boolean isMinusOrSimilar(char c) {
        return ((c == '−')
            || (c == '‒')
            || (c == '–')
            || (c == '—')
            || (c == '―')
            || (c == '‐')
            || (c == '-'));
    }
    
    protected int getIncrementForYearOfBirth() {
        return 150;
    }
    
    protected boolean inspectOnlyOneYear() {
        if ((dateLower.indexOf("z") > -1 && dateLower.indexOf("oz") < 0)
                || dateLower.indexOf("u") > -1
                || dateLower.indexOf("ú") > -1
                || dateLower.indexOf("ů") > -1
                || dateLower.indexOf("s") > -1
                || dateLower.indexOf("+") > -1
                || dateLower.indexOf("#") > -1
                /*
                vypustka, tecka, minus a ruzne druhy pomlcek a spojovniku: 
                System.out.println("…" + (int)'…');
                System.out.println("." + (int)'.');
                System.out.println("−" + (int)'−');
                System.out.println("‒" + (int)'‒');
                System.out.println("–" + (int)'–');
                System.out.println("—" + (int)'—');
                System.out.println("―" + (int)'―');
                System.out.println("‐" + (int)'‐');
                System.out.println("-" + (int)'-');
                ----------------
                …8230
                .46
                −8722
                ‒8210
                –8211
                —8212
                ―8213
                ‐8208
                -45
                 */
                || isNearBeforeYearLast("…")
                || isNearBeforeYearLast("..")
                || isNearBeforeYearLast("−")
                || isNearBeforeYearLast("‒")
                || isNearBeforeYearLast("–")
                || isNearBeforeYearLast("—")
                || isNearBeforeYearLast("―")
                || isNearBeforeYearLast("‐")
                || isNearBeforeYearLast("-")
                ) {
            return evaluateYear(false, yearLast);
        } else {
            return evaluateYear(true, yearLast);
        }
    }
    
    protected DateAuthorEvaluator(String date, int years) {
        super();
        this.dateLower = date.trim().toLowerCase();
        this.years = years;
    }

    /*
    narozen 1950
    nar. 1950
    1950 nar.
    1950-
    červen 1950
    2.2.1950
    1950
    7/1950
    7.1950
    *1950
    
    01234 ... index pozice
    +1950
    z1950
    zemř. 7.1950
    -1950
    1950 zemř.
    
    1930-2010
    1930 až 2010
    2.2.1850-3.3.1930
    ca1920-ca1990
    nar. 1750-1760 zemř. 1795-1810
    
    nar. 1795-1810
    zemř. 1795-1810
    nar. 1795 zemř. 1810
    */
    protected boolean isDateOk() {
        Boolean r = null;
        if (dateLower != null && dateLower.length() > 2) {
            for (int i = dateLower.length() - 1; i > 1; i--) {
                if (Character.isDigit(dateLower.charAt(i))
                        && Character.isDigit(dateLower.charAt(i - 1))
                        && Character.isDigit(dateLower.charAt(i - 2))) {
                    if (dateLower.length() == 3) {
                        r = evaluateYear(true, dateLower);
                    } else {
                        if (i > 2) {
                            if (Character.isDigit(dateLower.charAt(i - 3))) {
                                if (dateLower.length() == 4) {
                                    r = evaluateYear(true, dateLower);
                                } else { 
                                    i = addYear(i, 4);
                                }
                            } else {
                                i = addYear(i, 3);
                            }
                        } else {
                            i = addYear(i, 3);
                        }
                    }
                }
            }
            
            if (r == null) {
                if (yearCount > 0) {
                    if (yearCount == 1) {
                        r = inspectOnlyOneYear();
                    } else {
                        if (Integer.valueOf(yearLast) > Integer.valueOf(yearBeforeLast)) {
                            r = evaluateYear(false, yearLast);
                        } else {
                            r = evaluateYear(false, yearBeforeLast);
                        }
                    }
                }
            }
            
        }
        
        if (r == null) {
            r = false;
        }
        return r;
    }
    
    //yearLength 3 or 4
    private int addYear(int i, int yearLength) {
        yearCount++;
        if (yearLast == null) {
            ixYearLast = (i - yearLength) + 1;
            yearLast = dateLower.substring(ixYearLast, i + 1);
        } else {
            if (yearBeforeLast == null) {
                yearBeforeLast = dateLower.substring((i - yearLength) + 1, i + 1);
            }
        }
        return (i - yearLength);
    }
    
    protected boolean evaluateYear(boolean birth, String year) {
        int y = Integer.valueOf(year);
        int yNow = Calendar.getInstance().get(Calendar.YEAR);
                
        if (birth) {
            y = y + getIncrementForYearOfBirth();
        }
        
        if (y < yNow - years) {
            return true;
        }
        return false;
    }
    
    private boolean isNearBeforeYearLast(String s) {
        return dateLower.indexOf(s) == ixYearLast - s.length() - 2
                || dateLower.indexOf(s) == ixYearLast - s.length() - 3
                || dateLower.indexOf(s) == ixYearLast - s.length() - 4;
    }
    
}





















































