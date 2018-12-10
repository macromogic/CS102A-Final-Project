package TaskC;

import TaskB.Canvas;
import TaskB.Formats;
import TaskB.HistogramBase;
import TaskB.HistogramData;

public class HistogramC extends HistogramBase {

    private static final int TOTAL_BARS = 16;

    public HistogramC(Canvas c, Formats f, HistogramData d) {
        super(c, f, d);

        // TODO Margins data may need changes.
        f.margins[NORTH] = 0.2;
        f.margins[SOUTH] = 0.2;
        f.margins[WEST] = 0.14;
        f.margins[EAST] = 0.14;

        setHistogramParameters();
    }

    @Override
    protected void setHistogramParameters() {
        yValue[MIN] = -1;
        yValue[MAX] = d.values.length;

        xValue[MIN] = d.minValue;
        xValue[MAX] = d.values[d.mapIndex(0)];

        double max = xValue[MAX];
        double span = max - xValue[MIN];
        double factor = 1.0;
        if (span >= 1)
            while (span >= 10) {
                span /= 10;
                factor *= 10;
            }
        else
            while (span < 1) {
                span *= 10;
                factor /= 10;
            }
        int nSpan = (int) Math.floor(span);
        switch (nSpan) {
        case 1:
            rulerGrade = 5;
            rulerStep = factor / 5;
            break;
        case 2:
        case 3:
            rulerGrade = nSpan * 2;
            rulerStep = factor / 2;
            break;
        default:
            rulerGrade = nSpan;
            rulerStep = factor;
            break;
        }
    }

    @Override
    public void draw() {
        setCanvas();
        plotRuler();
        plotBars();
        plotKeys();
        if (f.hasHeader)
            plotHeader();
        if (f.hasFooter)
            plotFooter();
    }

    @Override
    protected void setCanvas() {
        StdDraw.setCanvasSize(c.x, c.y);
        setOriginalScale();
        StdDraw.clear(c.bgColor);
        StdDraw.setPenColor(c.color);
    }

    @Override
    protected void setHistogramScale(int nBars) {
        double span = xValue[MAX] - xValue[MIN] + 1;
        double xSpacing = span / (1 - f.margins[WEST] - f.margins[EAST]);
        xScale[MIN] = xValue[MIN] - f.margins[WEST] * xSpacing - 1;
        xScale[MAX] = xValue[MAX] + f.margins[EAST] * xSpacing;
        StdDraw.setXscale(xScale[MIN], xScale[MAX]);

        double ySpacing = (nBars + 1) / (1 - f.margins[NORTH] - f.margins[SOUTH]);
        yScale[MIN] = -f.margins[SOUTH] * ySpacing - 1;
        yScale[MAX] = nBars + f.margins[NORTH] * ySpacing;
        StdDraw.setYscale(yScale[MIN], yScale[MAX]);
    }

    @Override
    protected void setOriginalScale() {
        StdDraw.setXscale(c.xScale[MIN], c.xScale[MAX]);
        StdDraw.setYscale(c.yScale[MIN], c.yScale[MAX]);
    }

    @Override
    protected void plotRuler() {
        setHistogramScale(TOTAL_BARS);

        StdDraw.setFont(f.rulerFont);
        StdDraw.setPenColor(f.rulerColor);
        final double y0 = yValue[MIN], y1 = yValue[MAX];
        String[] mark = new String[rulerGrade + 1];
        for (int i = 0; i <= rulerGrade; i++) {
            double x = xValue[MIN] + i * rulerStep;
            mark[i] = numberForRuler(x);
            StdDraw.line(x, y0, x, y1);
        }
        int len = maxMarkLength(mark);
        final double ys = yScale[MIN] + 0.7 * (yValue[MIN] - yScale[MIN]);
        for (int i = 0; i <= rulerGrade; i++) {
            double x = xValue[MIN] + i * rulerStep;
            StdDraw.text(x, ys, String.format("%" + len + "s", mark[i]));
        }
    }

    @Override
    protected String numberForRuler(double x) {
        if (xValue[MAX] >= 5 && rulerStep > 1)
            return "" + (int) x;
        if (rulerStep > 0.1)
            return String.format("%.1f", x);
        if (rulerStep > 0.01)
            return String.format("%.2f", x);
        if (rulerStep > 0.001)
            return String.format("%.3f", x);
        if (rulerStep > 0.0001)
            return String.format("%.4f", x);
        if (rulerStep > 0.00001)
            return String.format("%.5f", x);
        return String.format("%g", x);
    }

    @Override
    protected void plotBars() {
        final double halfHeight = 0.25;
        // Assume that the bars are filled and have no frames.
        // TODO Customize color for each data.
        for (int j = 0; j < TOTAL_BARS; ++j) {
            int index = d.mapIndex(j);
            double halfWidth = d.values[index] / 2;
            // TODO Linear interpolation.
            double yPosition = TOTAL_BARS - j; // what looks like `position[index]`
            StdDraw.setPenColor(f.barFillColor); // what looks like `color[index]`
            StdDraw.filledRectangle(halfWidth, yPosition, halfWidth, halfHeight);
        }
    }

    @Override
    protected void plotKeys() {
        StdDraw.setFont(f.keysFont);
        // TODO Customize color for each data.
        for (int j = 0; j < TOTAL_BARS; ++j) {
            int index = d.mapIndex(j);
            StdDraw.setPenColor(f.keyColor); // what looks like 'color[index]`
            double xPosition = xValue[MIN] - d.keys[index].length() / 2 - 0.1; // Not tested!
            // TODO Linear interpolation.
            double yPosition = TOTAL_BARS - j; // what looks like `position[index]`
            StdDraw.text(xPosition, yPosition, d.keys[index]);
        }
    }

    @Override
    protected void plotHeader() {
        StdDraw.setFont(f.headerFont);
        StdDraw.setPenColor(f.headerColor);
        if (f.headerLocation == null) {
            double x = .5 * (xScale[MIN] + xScale[MAX]);
            double y = .5 * (yValue[MAX] + yScale[MAX]);
            StdDraw.text(x, y, d.header);
        } else {
            StdDraw.text(f.headerLocation[0], f.headerLocation[1], d.header);
        }
    }

    @Override
    protected void plotFooter() {
        // TODO May plot year number?
    }

    // These methods will not be used in Task C.
    @Override
    protected void plotLegends() {
    }

    @Override
    protected void plotBorder() {
    }

    @Override
    protected void plotRightRuler() {
    }

}