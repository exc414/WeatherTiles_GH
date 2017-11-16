package io.bluephoenix.weathertiles.core.common;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public enum Operator
{
    EQ  { public boolean apply(int a, int b) { return a == b; } }, //Equal to
    NEQ { public boolean apply(int a, int b) { return a != b; } }, //Not equal to
    GT  { public boolean apply(int a, int b) { return a > b; } },  //Greater than
    GTE { public boolean apply(int a, int b) { return a >= b; } }, //Greater than or equal to
    LT  { public boolean apply(int a, int b) { return a < b; } },  //Less than
    LTE { public boolean apply(int a, int b) { return a <= b; } }; //Less than or equal to

    public abstract boolean apply(int a, int b);
}
