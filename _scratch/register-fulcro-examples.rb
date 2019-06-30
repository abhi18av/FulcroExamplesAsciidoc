(require_relative './fulcro-examples-extension.rb')

Asciidoctor::Extensions.register do
  if (@document.basebackend? 'html') && (@document.safe < SafeMode::SECURE)
    block_macro FulcroExamplesBlockMacro
  end
end


