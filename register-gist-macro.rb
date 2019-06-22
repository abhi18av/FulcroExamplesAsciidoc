(require_relative './gist-macro-extension.rb')

Asciidoctor::Extensions.register do
  if (@document.basebackend? 'html') && (@document.safe < SafeMode::SECURE)
    block_macro GistBlockMacro
  end
end

