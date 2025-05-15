package org.example.voxlink_backend.Service;
import org.example.voxlink_backend.Model.Ramal;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.RamalRepository;
import org.example.voxlink_backend.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RamalService {

    @Autowired
    private RamalRepository ramalRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método para criar um ramal
    public String criarRamal(Ramal ramal) {
        // Verifica se já existe um ramal ativo com o mesmo número
        Optional<Ramal> ramalExistente = ramalRepository.findByNumeroAndAtivo(ramal.getNumero(), true);
        if (ramalExistente.isPresent()) {
            return "Ramal com esse número já existe e está ativo.";
        }

        // Se o ramal deve ser associado a um usuário, verifica se o usuário existe
        if (ramal.getUsuario() != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(ramal.getUsuario().getId());
            if (usuarioOpt.isEmpty()) {
                return "Usuário não encontrado para associar ao ramal.";
            }
        }

        // Salva o ramal no banco de dados
        ramalRepository.save(ramal);
        return "Ramal criado com sucesso.";
    }

    // Buscar ramais disponíveis (ativos e não associados a nenhum usuário)
    public List<Ramal> buscarRamaisDisponiveis() {
        return ramalRepository.findByAtivoAndUsuarioIsNull(true);
    }

    // Buscar ramais indisponíveis (ativos e associados a algum usuário)
    public List<Ramal> buscarRamaisIndisponiveis() {
        return ramalRepository.findByAtivoAndUsuarioIsNotNull(true);
    }

    // Buscar todos os ramais (somente ativos)
    public List<Ramal> buscarTodosOsRamais() {
        return ramalRepository.findByAtivoTrue();
    }

    // Buscar ramal por número (exclui os inativos)
    public Optional<Ramal> buscarPorNumero(Integer numero) {
        return ramalRepository.findByNumeroAndAtivo(numero, true);
    }

    // Buscar todos os ramais inativos (somente para o gerente)
    public List<Ramal> buscarRamaisInativos() {
        return ramalRepository.findByAtivoFalse();
    }

    // Reativar ramal (somente para o gerente)
    public String reativarRamal(Long ramalId) {
        Optional<Ramal> ramalOpt = ramalRepository.findById(ramalId);

        if (ramalOpt.isEmpty()) {
            return "Ramal não encontrado.";
        }

        Ramal ramal = ramalOpt.get();
        ramal.setAtivo(true);  // Reativa o ramal
        ramalRepository.save(ramal);
        return "Ramal reativado com sucesso.";
    }

    // Desativar ramal (somente para o gerente)
    public String desativarRamal(Long ramalId) {
        Optional<Ramal> ramalOpt = ramalRepository.findById(ramalId);

        if (ramalOpt.isEmpty()) {
            return "Ramal não encontrado.";
        }

        Ramal ramal = ramalOpt.get();
        ramal.setAtivo(false);  // Desativa o ramal
        ramalRepository.save(ramal);
        return "Ramal desativado com sucesso.";
    }

    public String associarUsuarioARamal(String codigoLogin, Integer numero) {
        // Buscar o ramal pelo número e verificar se está ativo
        Optional<Ramal> ramalOpt = ramalRepository.findByNumeroAndAtivo(numero, true);
        if (ramalOpt.isEmpty()) {
            return "Ramal não encontrado ou está inativo.";
        }

        Ramal ramal = ramalOpt.get();

        // Verifica se o ramal já está associado a um usuário
        if (ramal.getUsuario() != null) {
            return "Ramal já está ocupado.";
        }

        // Buscar o usuário pelo código de login
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCodigoLogin(codigoLogin);
        if (usuarioOpt.isEmpty()) {
            return "Usuário não encontrado.";
        }

        // Associa o usuário ao ramal
        ramal.setUsuario(usuarioOpt.get());
        ramalRepository.save(ramal);

        return "Usuário associado ao ramal com sucesso.";
    }

    // Desassociar um ramal a um usuário
    public String desassociarUsuarioDoRamal(String codigoLogin, Integer numeroRamal) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCodigoLogin(codigoLogin);

        if (usuarioOpt.isEmpty()) {
            return "Usuário não encontrado.";
        }

        Usuario usuario = usuarioOpt.get();

        Optional<Ramal> ramalOpt = ramalRepository.findByNumeroAndUsuario(numeroRamal, usuario);
        if (ramalOpt.isEmpty()) {
            return "Ramal não encontrado ou não está associado a este usuário.";
        }

        Ramal ramal = ramalOpt.get();
        ramal.setUsuario(null);
        ramalRepository.save(ramal);

        return "Ramal desassociado com sucesso.";
    }

    public List<Ramal> buscarTodosPorUsuario(Usuario usuario) {
        return ramalRepository.findByUsuario(usuario);
    }


    public String desativarRamalPorNumero(Integer numeroRamal) {
        Optional<Ramal> ramalOpt = ramalRepository.findByNumeroAndAtivo(numeroRamal, true);
        if (ramalOpt.isEmpty()) {
            throw new RuntimeException("Ramal não encontrado ou já está inativo.");
        }

        Ramal ramal = ramalOpt.get();
        ramal.setAtivo(false);
        ramalRepository.save(ramal);
        return "Ramal desativado com sucesso!";
    }
















}
