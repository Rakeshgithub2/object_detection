/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voice.speech;

/**
 *
 * @author SUHAS ACHARYA S
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.speech.*;    
import java.util.*;    
import javax.speech.synthesis.*;  

/**
 *
 * @author SUHAS ACHARYA S
 */
public class demojsapi
{   
String speaktext; 
public void dospeak(String speak,String  voicename)    
{    
   speaktext=speak;
    String voiceName =voicename;

    try
    {   System.setProperty("FreeTTSSynthEngineCentral", "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
        SynthesizerModeDesc desc = new SynthesizerModeDesc(null,"general",  Locale.US,null,null);
        Synthesizer synthesizer =  Central.createSynthesizer(desc);
        EngineList engineList = Central.availableSynthesizers(desc);

        EngineCreate creator = (EngineCreate)engineList.get(0);

        List<Synthesizer> bagOfSynthesizers = new LinkedList<Synthesizer>();

        int numberSynthesizers=1;
        for (int i = 0; i < numberSynthesizers; i++) {
            synthesizer = (Synthesizer)creator.createEngine();
            bagOfSynthesizers.add(synthesizer);
            synthesizer.allocate();
            synthesizer.resume();
            desc = (SynthesizerModeDesc)  synthesizer.getEngineModeDesc();
        }




        Voice[] voices = desc.getVoices();
        Voice voice = null;
        for (int i = 0; i < voices.length; i++)
        {
            if (voices[i].getName().equals(voiceName))
            {
                voice = voices[i];
                break;
            }
        }
        synthesizer.getSynthesizerProperties().setVoice(voice);
        System.out.print("Speaking : "+speaktext);
        synthesizer.speakPlainText(speaktext, null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();


    }
    catch (Exception e)
    {
        String message = " missing speech.properties in " + System.getProperty("user.home") + "\n";
        System.out.println(""+e);
        System.out.println(message);
    }

}    


}


