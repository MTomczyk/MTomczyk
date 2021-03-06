package patterns.weights;

import org.apache.commons.math3.random.MersenneTwister;
import simplex.WeightsGenerator;

/**
 * Created by Michał on 2015-02-17.
 */
public class weights_3d
{
    public static void main(String args[])
    {
        double tmp[][] = new double[200][3];
        MersenneTwister g = new MersenneTwister(System.currentTimeMillis());

        for (int i = 0; i < 200; i++)
            tmp[i] = WeightsGenerator.getUniformWeights(3, g);

        System.out.println("{");
        for (int i = 0; i < 200; i++)
        {
            System.out.print("{");
            String s1 = String.format("%.8fd", tmp[i][0]);
            String s2 = String.format("%.8fd", tmp[i][1]);
            String s3 = String.format("%.8fd", tmp[i][2]);

            System.out.println(s1 + "," + s2 + "," + s3 + "},");
        }

        System.out.println("};");
    }

    public static double data[][] =
            {
                    {0.41818413d,0.48863458d,0.09318129d},
                    {0.22624852d,0.43118430d,0.34256718d},
                    {0.45516778d,0.03505986d,0.50977236d},
                    {0.55188254d,0.04969292d,0.39842454d},
                    {0.10115065d,0.84924789d,0.04960146d},
                    {0.27471587d,0.20020632d,0.52507781d},
                    {0.46160343d,0.15020543d,0.38819113d},
                    {0.25804320d,0.65315821d,0.08879860d},
                    {0.15671393d,0.23855613d,0.60472994d},
                    {0.18537626d,0.43730405d,0.37731969d},
                    {0.05453757d,0.01179625d,0.93366618d},
                    {0.08934538d,0.25230738d,0.65834724d},
                    {0.32496739d,0.06566233d,0.60937027d},
                    {0.55600602d,0.30899486d,0.13499912d},
                    {0.35408517d,0.25533117d,0.39058367d},
                    {0.33118711d,0.06281604d,0.60599685d},
                    {0.05158664d,0.75433639d,0.19407697d},
                    {0.16173182d,0.79366089d,0.04460730d},
                    {0.52797004d,0.06176764d,0.41026233d},
                    {0.04233526d,0.44525058d,0.51241416d},
                    {0.10904184d,0.09141577d,0.79954240d},
                    {0.56214849d,0.22066443d,0.21718708d},
                    {0.32103370d,0.15182277d,0.52714354d},
                    {0.44804459d,0.30516672d,0.24678869d},
                    {0.18193175d,0.67143648d,0.14663177d},
                    {0.37484018d,0.01218381d,0.61297601d},
                    {0.83734011d,0.04488218d,0.11777770d},
                    {0.69858332d,0.06808198d,0.23333470d},
                    {0.00715018d,0.01658224d,0.97626757d},
                    {0.11718327d,0.14561808d,0.73719865d},
                    {0.70151152d,0.29816610d,0.00032238d},
                    {0.27608721d,0.70301493d,0.02089786d},
                    {0.76923499d,0.08529892d,0.14546609d},
                    {0.06694665d,0.68705465d,0.24599870d},
                    {0.52980132d,0.25265800d,0.21754069d},
                    {0.67981600d,0.20599433d,0.11418967d},
                    {0.08177138d,0.12132300d,0.79690563d},
                    {0.48005530d,0.42736451d,0.09258019d},
                    {0.34961109d,0.21395118d,0.43643773d},
                    {0.50160647d,0.20886456d,0.28952897d},
                    {0.09016534d,0.72915622d,0.18067844d},
                    {0.41200955d,0.31738947d,0.27060098d},
                    {0.26409175d,0.47236508d,0.26354317d},
                    {0.04657881d,0.45601850d,0.49740269d},
                    {0.22303666d,0.52685087d,0.25011247d},
                    {0.01727818d,0.51428090d,0.46844092d},
                    {0.32758312d,0.08909585d,0.58332103d},
                    {0.55563717d,0.35486545d,0.08949738d},
                    {0.36128948d,0.00585511d,0.63285541d},
                    {0.66994132d,0.16300388d,0.16705480d},
                    {0.01358541d,0.80415229d,0.18226230d},
                    {0.81110234d,0.11878255d,0.07011510d},
                    {0.76076630d,0.16747861d,0.07175509d},
                    {0.81691276d,0.12874730d,0.05433994d},
                    {0.29418175d,0.56161394d,0.14420431d},
                    {0.32604811d,0.47583993d,0.19811196d},
                    {0.26062417d,0.01977220d,0.71960363d},
                    {0.18573077d,0.70488974d,0.10937950d},
                    {0.04844255d,0.44101339d,0.51054406d},
                    {0.82768887d,0.12862181d,0.04368932d},
                    {0.36386791d,0.27180951d,0.36432258d},
                    {0.35203038d,0.28603756d,0.36193207d},
                    {0.34428600d,0.36902952d,0.28668448d},
                    {0.37059675d,0.49283253d,0.13657072d},
                    {0.09239679d,0.85806151d,0.04954170d},
                    {0.81683550d,0.16375671d,0.01940779d},
                    {0.06235796d,0.14982294d,0.78781910d},
                    {0.86371595d,0.12611828d,0.01016577d},
                    {0.12812362d,0.19344561d,0.67843077d},
                    {0.02168175d,0.08889699d,0.88942126d},
                    {0.25208474d,0.25652937d,0.49138590d},
                    {0.37917181d,0.44537627d,0.17545191d},
                    {0.11691909d,0.53425662d,0.34882430d},
                    {0.41119430d,0.41834664d,0.17045906d},
                    {0.42433093d,0.16600938d,0.40965969d},
                    {0.21611184d,0.21695441d,0.56693375d},
                    {0.18062108d,0.45603134d,0.36334758d},
                    {0.35708382d,0.07567870d,0.56723748d},
                    {0.19500325d,0.71083151d,0.09416524d},
                    {0.16269432d,0.29953193d,0.53777375d},
                    {0.05472879d,0.29683232d,0.64843889d},
                    {0.26304282d,0.48098503d,0.25597214d},
                    {0.05390209d,0.49981373d,0.44628418d},
                    {0.11923779d,0.44322073d,0.43754148d},
                    {0.31266092d,0.28420974d,0.40312934d},
                    {0.33980826d,0.28056847d,0.37962327d},
                    {0.84661147d,0.03110268d,0.12228585d},
                    {0.59060396d,0.37836574d,0.03103030d},
                    {0.26800173d,0.32141362d,0.41058465d},
                    {0.46330541d,0.50761120d,0.02908340d},
                    {0.62258852d,0.17947116d,0.19794033d},
                    {0.26693327d,0.66654889d,0.06651784d},
                    {0.35874462d,0.29731935d,0.34393603d},
                    {0.03522268d,0.89973488d,0.06504245d},
                    {0.28364975d,0.16271870d,0.55363155d},
                    {0.61544653d,0.17549241d,0.20906106d},
                    {0.03197371d,0.07350660d,0.89451969d},
                    {0.11958115d,0.09923318d,0.78118567d},
                    {0.66654042d,0.15351872d,0.17994086d},
                    {0.66523405d,0.17531310d,0.15945285d},
                    {0.68447758d,0.03398475d,0.28153767d},
                    {0.06432425d,0.50748203d,0.42819372d},
                    {0.20977803d,0.41163422d,0.37858775d},
                    {0.62076629d,0.34948539d,0.02974832d},
                    {0.69634291d,0.29618914d,0.00746795d},
                    {0.40278294d,0.30991708d,0.28729998d},
                    {0.09486155d,0.63490616d,0.27023229d},
                    {0.15407114d,0.02403672d,0.82189214d},
                    {0.40692750d,0.13728915d,0.45578335d},
                    {0.25773434d,0.64163857d,0.10062708d},
                    {0.32765852d,0.47177948d,0.20056200d},
                    {0.18807777d,0.07647718d,0.73544505d},
                    {0.32654735d,0.29857523d,0.37487742d},
                    {0.38434496d,0.44984638d,0.16580866d},
                    {0.72964253d,0.17317217d,0.09718530d},
                    {0.21245398d,0.11892872d,0.66861730d},
                    {0.24889615d,0.12281981d,0.62828404d},
                    {0.32561577d,0.20159259d,0.47279165d},
                    {0.47956965d,0.04302342d,0.47740693d},
                    {0.33474615d,0.36031175d,0.30494210d},
                    {0.57587170d,0.25429977d,0.16982853d},
                    {0.59149385d,0.04538572d,0.36312043d},
                    {0.46168755d,0.42984050d,0.10847195d},
                    {0.16012478d,0.31925807d,0.52061715d},
                    {0.64863732d,0.10901025d,0.24235243d},
                    {0.84009450d,0.10466348d,0.05524202d},
                    {0.31283316d,0.49961023d,0.18755661d},
                    {0.13341292d,0.37110995d,0.49547714d},
                    {0.46752558d,0.48520806d,0.04726637d},
                    {0.17008062d,0.31839973d,0.51151965d},
                    {0.31106010d,0.18808942d,0.50085048d},
                    {0.13465991d,0.41546180d,0.44987829d},
                    {0.43184036d,0.06753016d,0.50062948d},
                    {0.02079139d,0.11964024d,0.85956838d},
                    {0.64009688d,0.31684468d,0.04305844d},
                    {0.29840578d,0.48328229d,0.21831193d},
                    {0.93699709d,0.02038287d,0.04262003d},
                    {0.18912758d,0.33075609d,0.48011633d},
                    {0.66619740d,0.11920502d,0.21459757d},
                    {0.04606729d,0.34231069d,0.61162201d},
                    {0.20389585d,0.64159404d,0.15451011d},
                    {0.16390694d,0.07023802d,0.76585504d},
                    {0.24378312d,0.32247568d,0.43374120d},
                    {0.15056718d,0.58193319d,0.26749964d},
                    {0.29080888d,0.20592174d,0.50326939d},
                    {0.50751830d,0.08065884d,0.41182286d},
                    {0.39285881d,0.59607376d,0.01106743d},
                    {0.51353916d,0.38247463d,0.10398621d},
                    {0.21774174d,0.18627749d,0.59598076d},
                    {0.26371327d,0.38919364d,0.34709309d},
                    {0.28640610d,0.36558507d,0.34800884d},
                    {0.48619815d,0.01493217d,0.49886968d},
                    {0.01438480d,0.50844527d,0.47716993d},
                    {0.24505283d,0.59900196d,0.15594521d},
                    {0.43205598d,0.06012840d,0.50781562d},
                    {0.17342379d,0.12649923d,0.70007699d},
                    {0.30728315d,0.29444924d,0.39826761d},
                    {0.56560360d,0.04438270d,0.39001369d},
                    {0.35543550d,0.50988130d,0.13468320d},
                    {0.01293450d,0.52506330d,0.46200221d},
                    {0.02521818d,0.05925663d,0.91552519d},
                    {0.00473797d,0.14331390d,0.85194813d},
                    {0.47037650d,0.23099239d,0.29863112d},
                    {0.43818697d,0.20999221d,0.35182082d},
                    {0.28462289d,0.19989882d,0.51547830d},
                    {0.01998977d,0.53208208d,0.44792815d},
                    {0.56862378d,0.08617175d,0.34520447d},
                    {0.93616465d,0.04957897d,0.01425638d},
                    {0.03355453d,0.66675406d,0.29969142d},
                    {0.78167559d,0.11166450d,0.10665991d},
                    {0.32983381d,0.45476784d,0.21539835d},
                    {0.11357184d,0.87264989d,0.01377827d},
                    {0.41301992d,0.31790084d,0.26907924d},
                    {0.06633105d,0.79166329d,0.14200566d},
                    {0.27819189d,0.28598100d,0.43582711d},
                    {0.48349759d,0.19330967d,0.32319274d},
                    {0.23895330d,0.23613570d,0.52491100d},
                    {0.01451475d,0.86039921d,0.12508604d},
                    {0.82384692d,0.03025986d,0.14589322d},
                    {0.31068142d,0.17543115d,0.51388743d},
                    {0.04275962d,0.71512015d,0.24212022d},
                    {0.59671458d,0.16854410d,0.23474132d},
                    {0.05224739d,0.52176196d,0.42599064d},
                    {0.22764560d,0.21302160d,0.55933280d},
                    {0.33439862d,0.57552891d,0.09007247d},
                    {0.61961632d,0.03066688d,0.34971680d},
                    {0.53295227d,0.22514464d,0.24190309d},
                    {0.68400944d,0.28983500d,0.02615556d},
                    {0.80738584d,0.15306338d,0.03955078d},
                    {0.20601838d,0.77474898d,0.01923265d},
                    {0.03214610d,0.16223361d,0.80562029d},
                    {0.11134604d,0.72996333d,0.15869063d},
                    {0.45569897d,0.13379898d,0.41050205d},
                    {0.10108415d,0.51381039d,0.38510546d},
                    {0.17875846d,0.54308061d,0.27816093d},
                    {0.04974249d,0.60380162d,0.34645589d},
                    {0.04403632d,0.70460868d,0.25135500d},
                    {0.08381816d,0.16509324d,0.75108860d},
                    {0.05845207d,0.92286804d,0.01867989d},
                    {0.64518449d,0.34385124d,0.01096427d},
            };



}
