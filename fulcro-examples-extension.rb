# A block macro that embeds a Fulcro example into the output document
#
# Usage
#
#   $example$ "victory_name", "victory_id", "./src/victory_example.cljs"]
#
##############
#   Which in turn gets converted into
##############
=begin

  .[[UsingExternalReactLibraries]]<<UsingExternalReactLibraries,Using External React Libraries>>
  ====
  ++++
  <button class="inspector" onClick="book.main.focus('victory-example')">Focus Inspector</button>
  <div class="short narrow example" id="victory-example"></div>
  <br/>
  ++++
  [source,clojure,role="source"]
  ----
  include::src/book/book/ui/victory_example.cljs[]
  ----
  ====

=end


require 'asciidoctor/extensions'

include Asciidoctor




def create_asciidoc_block example_name, example_id, example_source
  return %{
.[[SampleExample]]<<SampleExample,Sample Example>>
====
++++
<button class="inspector" onClick="book.main.focus('#{example_name}')">Focus Inspector</button>
<div class="short narrow example" id="#{example_id}"></div>
<br/>
++++
[source,clojure,role="source"]
----
include::#{example_source}[]
----
====
}
end



class FulcroPreprocessor < Extensions::Preprocessor
  def process document, reader
    return reader if reader.eof?

    replacement_lines = reader.read_lines.map do |line|
      if(line.include? '$example$')

        parameters = line.split("$")[2]
        example_name = parameters.split(",")[0].strip()
        example_id = parameters.split(",")[1].strip()
        example_source =  parameters.split(",")[2].strip()
        example_block = create_asciidoc_block(example_name, example_id, example_source)

        (line.gsub line, example_block)

      else
        line
      end
    end
    # TODO Remove the generation of the intermediate Expanded file
    reader.unshift_all(replacement_lines)
    File.open("DevelopersGuide.adoc", "w") { |file| file.puts reader.lines}
    reader
  end
end
