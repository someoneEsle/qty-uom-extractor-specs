package com.omnius.challenges.extractors.qtyuom.utils;

import com.omnius.challenges.extractors.qtyuom.QtyUomExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements {@link QtyUomExtractor} identifying as <strong>the most relevant UOM</strong> the leftmost UOM found in the articleDescription.
 * The {@link UOM} array contains the list of valid UOMs. The algorithm search for the leftmost occurence of UOM[i], if there are no occurrences then tries UOM[i+1].
 *
 * Example
 * <ul>
 * <li>article description: "black steel bar 35 mm 77 stck"</li>
 * <li>QTY: "77" (and NOT "35")</li>
 * <li>UOM: "stck" (and not "mm" since "stck" has an higher priority as UOM )</li>
 * </ul>
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 4 May 2018
 */
public class LeftMostUOMExtractor implements QtyUomExtractor {

    /**
     * Array of valid UOM to match. the elements with lower index in the array has higher priority
     */
    public static String[] UOM = {"stk", "stk.", "stck", "stück", "stg", "stg.", "st", "st.", "stange", "stange(n)", "tafel", "tfl", "taf", "mtr", "meter", "qm", "kg", "lfm", "mm", "m"};

    public LeftMostUOMExtractor() {}

    @Override
    public Pair<String, String> extract(String articleDescription) {
        if (articleDescription == null) {
            return null;
        }
        //patterns to extract QTY
        String patternQTY = "(^|\\s+)\\d{1,3}((,|\\s)\\d{3})*(\\s*(.|,)\\s*\\d+)?";
        String patternPair;
        Pattern pattern;
        Matcher matcher;
        String matchingString;
        for (String uom : UOM) {
            //combine qty and uom in one pattern
            patternPair = patternQTY + "\\s*(?i)" + uom + "(\\s+|$)";
            pattern = Pattern.compile(patternPair);
            matcher = pattern.matcher(articleDescription);
            if (matcher.find()) {
                //extract the string that matches the pattern
                matchingString = matcher.group();
                return new Pair<>(matchingString.substring(0, matchingString.length() - 1 - uom.length()).trim().
                        replace(" ", ""), uom);
            }
        }
        return null;
    }

    @Override
    public Pair<Double, String> extractAsDouble(String articleDescription) {
        Pair<String, String> pair = extract(articleDescription);
        return new Pair<>(Double.parseDouble(pair.getFirst()), pair.getSecond());
    }


}